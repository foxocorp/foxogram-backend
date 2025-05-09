package su.foxogram.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import su.foxogram.constants.EmailConstants;
import su.foxogram.constants.OTPConstants;
import su.foxogram.constants.UserConstants;
import su.foxogram.dtos.api.request.AttachmentsAddDTO;
import su.foxogram.dtos.api.request.UserEditDTO;
import su.foxogram.dtos.api.response.AttachmentsDTO;
import su.foxogram.dtos.api.response.ChannelDTO;
import su.foxogram.exceptions.message.AttachmentsCannotBeEmpty;
import su.foxogram.exceptions.message.UnknownAttachmentsException;
import su.foxogram.exceptions.otp.OTPExpiredException;
import su.foxogram.exceptions.otp.OTPsInvalidException;
import su.foxogram.exceptions.user.UserCredentialsDuplicateException;
import su.foxogram.exceptions.user.UserCredentialsIsInvalidException;
import su.foxogram.exceptions.user.UserNotFoundException;
import su.foxogram.models.*;
import su.foxogram.repositories.AttachmentRepository;
import su.foxogram.repositories.MemberRepository;
import su.foxogram.repositories.MessageRepository;
import su.foxogram.repositories.UserRepository;
import su.foxogram.util.OTPGenerator;
import su.foxogram.util.PasswordHasher;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
	private final UserRepository userRepository;

	private final EmailService emailService;

	private final OTPService OTPService;

	private final MemberRepository memberRepository;

	private final MessageRepository messageRepository;

	private final AttachmentService attachmentService;

	private final AttachmentRepository attachmentRepository;

	@Autowired
	public UserService(UserRepository userRepository, EmailService emailService, OTPService OTPService, MemberRepository memberRepository, MessageRepository messageRepository, AttachmentService attachmentService, AttachmentRepository attachmentRepository) {
		this.userRepository = userRepository;
		this.emailService = emailService;
		this.OTPService = OTPService;
		this.memberRepository = memberRepository;
		this.messageRepository = messageRepository;
		this.attachmentService = attachmentService;
		this.attachmentRepository = attachmentRepository;
	}

	public User getUserById(long id) throws UserNotFoundException {
		return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
	}

	public User getUserByUsername(String username) throws UserNotFoundException {
		return userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
	}

	public List<ChannelDTO> getChannels(User user) {
		return memberRepository.findAllByUserId(user.getId())
				.stream()
				.map(Member::getChannel).map(channel -> {
					Message lastMessage = messageRepository.getLastMessageByChannel(channel);
					return new ChannelDTO(channel, lastMessage);
				})
				.collect(Collectors.toList());
	}

	public User editUser(User user, UserEditDTO body) throws UserCredentialsDuplicateException, UnknownAttachmentsException {
		if (body.getDisplayName() != null) user.setDisplayName(body.getDisplayName());
		if (body.getUsername() != null) user.setUsername(body.getUsername());
		if (body.getEmail() != null) changeEmail(user, body);
		if (body.getPassword() != null) changePassword(user, body);
		if (body.getAvatar() <= 0) {
			Attachment attachment = attachmentRepository.findById(body.getAvatar());

			if (attachment == null) throw new UnknownAttachmentsException();

			user.setAvatar(attachment);
		}

		try {
			userRepository.save(user);
		} catch (DataIntegrityViolationException e) {
			throw new UserCredentialsDuplicateException();
		}

		log.debug("User ({}) edited successfully", user.getUsername());

		return user;
	}

	public AttachmentsDTO uploadAvatar(User user, AttachmentsAddDTO attachment) throws UnknownAttachmentsException, AttachmentsCannotBeEmpty {
		if (attachment == null) throw new AttachmentsCannotBeEmpty();

		return attachmentService.uploadAttachment(user, attachment);
	}

	public void requestUserDelete(User user, String password) throws UserCredentialsIsInvalidException {
		if (!PasswordHasher.verifyPassword(password, user.getPassword()))
			throw new UserCredentialsIsInvalidException();

		sendEmail(user, EmailConstants.Type.ACCOUNT_DELETE);
		log.debug("User ({}) delete requested successfully", user.getUsername());
	}

	public void confirmUserDelete(User user, String pathCode) throws OTPsInvalidException, OTPExpiredException {
		OTP OTP = OTPService.validateCode(pathCode);

		userRepository.delete(user);

		log.debug("User ({}) deleted successfully", user.getUsername());

		if (OTP == null) return; // is dev

		OTPService.delete(OTP);
	}

	private void changeEmail(User user, UserEditDTO body) {
		user.setEmail(body.getEmail());
		user.addFlag(UserConstants.Flags.AWAITING_CONFIRMATION);

		sendEmail(user, EmailConstants.Type.EMAIL_VERIFY);
	}

	private void changePassword(User user, UserEditDTO body) {
		user.setPassword(PasswordHasher.hashPassword(body.getPassword()));
		user.addFlag(UserConstants.Flags.AWAITING_CONFIRMATION);

		sendEmail(user, EmailConstants.Type.RESET_PASSWORD);
	}

	private void sendEmail(User user, EmailConstants.Type type) {
		String emailType = type.getValue();
		String code = OTPGenerator.generateDigitCode();
		long issuedAt = System.currentTimeMillis();
		long expiresAt = issuedAt + OTPConstants.Lifetime.BASE.getValue();

		emailService.sendEmail(user.getEmail(), user.getId(), emailType, user.getUsername(), code, issuedAt, expiresAt, null);
	}
}
