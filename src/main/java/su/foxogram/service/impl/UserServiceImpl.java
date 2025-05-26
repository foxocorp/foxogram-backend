package su.foxogram.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import su.foxogram.config.APIConfig;
import su.foxogram.constant.EmailConstant;
import su.foxogram.constant.GatewayConstant;
import su.foxogram.constant.OTPConstant;
import su.foxogram.constant.UserConstant;
import su.foxogram.dto.api.request.UserEditDTO;
import su.foxogram.dto.api.response.UserDTO;
import su.foxogram.dto.gateway.StatusDTO;
import su.foxogram.exception.message.UnknownAttachmentsException;
import su.foxogram.exception.otp.OTPExpiredException;
import su.foxogram.exception.otp.OTPsInvalidException;
import su.foxogram.exception.user.*;
import su.foxogram.model.Member;
import su.foxogram.model.OTP;
import su.foxogram.model.User;
import su.foxogram.model.UserContact;
import su.foxogram.repository.UserRepository;
import su.foxogram.service.*;
import su.foxogram.util.OTPGenerator;
import su.foxogram.util.PasswordHasher;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements su.foxogram.service.UserService {

	private final UserRepository userRepository;

	private final EmailService emailService;

	private final OTPService otpService;

	private final AttachmentService attachmentService;

	private final APIConfig apiConfig;

	private final GatewayService gatewayService;

	private final MemberService memberService;

	public UserServiceImpl(UserRepository userRepository, EmailService emailService, OTPService otpService, AttachmentService attachmentService, APIConfig apiConfig, GatewayService gatewayService, MemberService memberService) {
		this.userRepository = userRepository;
		this.emailService = emailService;
		this.otpService = otpService;
		this.attachmentService = attachmentService;
		this.apiConfig = apiConfig;
		this.gatewayService = gatewayService;
		this.memberService = memberService;
	}

	@Override
	public Optional<User> getById(long id) {
		return userRepository.findById(id);
	}

	@Override
	public Optional<User> getByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public Optional<User> getByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public void updateFlags(User user, UserConstant.Flags removeFlag, UserConstant.Flags addFlag) {
		user.removeFlag(removeFlag);
		user.addFlag(addFlag);
		userRepository.save(user);
	}

	@Override
	public User add(String username, String email, String password) throws UserCredentialsDuplicateException {
		long flags = UserConstant.Flags.AWAITING_CONFIRMATION.getBit();
		if (apiConfig.isDevelopment()) flags = UserConstant.Flags.EMAIL_VERIFIED.getBit();
		int type = UserConstant.Type.USER.getType();

		User user = new User(username, email, PasswordHasher.hashPassword(password), flags, type);

		try {
			userRepository.save(user);
		} catch (DataIntegrityViolationException e) {
			throw new UserCredentialsDuplicateException();
		}

		log.debug("Successfully created new user {}, {}", user.getId(), user.getUsername());
		return user;
	}

	@Override
	public User update(User user, UserEditDTO body) throws UserCredentialsDuplicateException, UnknownAttachmentsException {
		if (body.getDisplayName() != null) user.setDisplayName(body.getDisplayName());
		if (body.getUsername() != null) user.setUsername(body.getUsername());
		if (body.getEmail() != null) changeEmail(user, body);
		if (body.getPassword() != null) changePassword(user, body);
		if (body.getAvatar() != null) {
			user.setAvatar(attachmentService.getById(body.getAvatar()));
		}

		try {
			userRepository.save(user);
		} catch (DataIntegrityViolationException e) {
			throw new UserCredentialsDuplicateException();
		}

		log.debug("User {} edited successfully", user.getUsername());

		return user;
	}

	@Override
	public void requestDelete(User user, String password) throws UserCredentialsIsInvalidException {
		if (!PasswordHasher.verifyPassword(password, user.getPassword()))
			throw new UserCredentialsIsInvalidException();

		sendEmail(user, EmailConstant.Type.ACCOUNT_DELETE);
		log.debug("User {} delete requested successfully", user.getUsername());
	}

	@Override
	public void confirmDelete(User user, String pathCode) throws OTPsInvalidException, OTPExpiredException {
		OTP OTP = otpService.validate(pathCode);

		userRepository.delete(user);

		log.debug("User {} deleted successfully", user.getUsername());

		if (OTP == null) return; // if dev

		otpService.delete(OTP);
	}

	@Override
	public void setStatus(long userId, int status) throws Exception {
		User user = getById(userId).orElseThrow(UserNotFoundException::new);

		user.setStatus(status);
		user.setStatusUpdatedAt(System.currentTimeMillis());
		userRepository.save(user);

		List<Long> recipients = memberService.getChannelsByUserId(user.getId()).stream()
				.flatMap(channel -> channel.getMembers().stream())
				.map(Member::getId)
				.distinct()
				.collect(Collectors.toList());

		gatewayService.sendMessageToSpecificSessions(recipients, GatewayConstant.Opcode.DISPATCH.ordinal(), new StatusDTO(userId, status), GatewayConstant.Event.USER_STATUS_UPDATE.getValue());
		log.debug("Set user {} status {} successfully", user.getUsername(), status);
	}

	@Override
	public User addContact(User user, long id) throws UserContactAlreadyExistException {
		try {
			User contact = getById(id).orElseThrow(UserNotFoundException::new);
			user.getContacts().add(new UserContact(user, contact));
			userRepository.save(user);

			gatewayService.sendMessageToSpecificSessions(Collections.singletonList(contact.getId()), GatewayConstant.Opcode.DISPATCH.ordinal(), new UserDTO(user, null, null, false, false, false), GatewayConstant.Event.CONTACT_ADD.getValue());
			log.debug("Successfully added contact {} to user {}", contact.getId(), user.getId());
			return contact;
		} catch (DataIntegrityViolationException e) {
			throw new UserContactAlreadyExistException();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void deleteContact(User user, long id) throws UserContactNotFoundException {
		try {
			User contact = getById(id).orElseThrow(UserNotFoundException::new);
			user.getContacts().remove(new UserContact(user, contact));
			userRepository.save(user);

			gatewayService.sendMessageToSpecificSessions(Collections.singletonList(contact.getId()), GatewayConstant.Opcode.DISPATCH.ordinal(), new UserDTO(user, null, null, false, false, false), GatewayConstant.Event.CONTACT_DELETE.getValue());
			log.debug("Successfully deleted contact {} from user {}", contact.getId(), user.getId());
		} catch (DataIntegrityViolationException e) {
			throw new UserContactNotFoundException();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void changeEmail(User user, UserEditDTO body) {
		user.setEmail(body.getEmail());
		user.addFlag(UserConstant.Flags.AWAITING_CONFIRMATION);

		sendEmail(user, EmailConstant.Type.EMAIL_VERIFY);
		log.debug("Sent email request to change user {} email ({} -> {})", user.getUsername(), user.getEmail(), body.getEmail());
	}

	private void changePassword(User user, UserEditDTO body) {
		user.setPassword(PasswordHasher.hashPassword(body.getPassword()));
		user.addFlag(UserConstant.Flags.AWAITING_CONFIRMATION);

		sendEmail(user, EmailConstant.Type.RESET_PASSWORD);
		log.debug("Sent email request to change user {} password", user.getUsername());
	}

	private void sendEmail(User user, EmailConstant.Type type) {
		String emailType = type.getValue();
		String code = OTPGenerator.generateDigitCode();
		long issuedAt = System.currentTimeMillis();
		long expiresAt = issuedAt + OTPConstant.Lifetime.BASE.getValue();

		emailService.send(user.getEmail(), user.getId(), emailType, user.getUsername(), code, issuedAt, expiresAt, null);
	}
}
