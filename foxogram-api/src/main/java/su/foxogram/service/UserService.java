package su.foxogram.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import su.foxogram.config.APIConfig;
import su.foxogram.constant.EmailConstant;
import su.foxogram.constant.OTPConstant;
import su.foxogram.constant.UserConstant;
import su.foxogram.dto.api.request.UserEditDTO;
import su.foxogram.exception.message.UnknownAttachmentsException;
import su.foxogram.exception.otp.OTPExpiredException;
import su.foxogram.exception.otp.OTPsInvalidException;
import su.foxogram.exception.user.UserCredentialsDuplicateException;
import su.foxogram.exception.user.UserCredentialsIsInvalidException;
import su.foxogram.model.OTP;
import su.foxogram.model.User;
import su.foxogram.repository.UserRepository;
import su.foxogram.util.OTPGenerator;
import su.foxogram.util.PasswordHasher;

import java.util.Optional;

@Slf4j
@Service
public class UserService {

	private final UserRepository userRepository;

	private final EmailService emailService;

	private final OTPService otpService;

	private final AttachmentService attachmentService;

	private final APIConfig apiConfig;

	@Autowired
	public UserService(UserRepository userRepository, EmailService emailService, OTPService otpService, AttachmentService attachmentService, APIConfig apiConfig) {
		this.userRepository = userRepository;
		this.emailService = emailService;
		this.otpService = otpService;
		this.attachmentService = attachmentService;
		this.apiConfig = apiConfig;
	}

	public Optional<User> getById(long id) {
		return userRepository.findById(id);
	}

	public Optional<User> getByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	public Optional<User> getByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public void updateFlags(User user, UserConstant.Flags removeFlag, UserConstant.Flags addFlag) {
		user.removeFlag(removeFlag);
		user.addFlag(addFlag);
		userRepository.save(user);
	}

	public User add(String username, String email, String password) throws UserCredentialsDuplicateException {
		long deletion = 0;
		long flags = UserConstant.Flags.AWAITING_CONFIRMATION.getBit();
		if (apiConfig.isDevelopment()) flags = UserConstant.Flags.EMAIL_VERIFIED.getBit();
		int type = UserConstant.Type.USER.getType();

		User user = new User(0, null, username, email, PasswordHasher.hashPassword(password), flags, type, deletion, null);

		try {
			userRepository.save(user);
		} catch (DataIntegrityViolationException e) {
			throw new UserCredentialsDuplicateException();
		}

		return user;
	}

	public User update(User user, UserEditDTO body) throws UserCredentialsDuplicateException, UnknownAttachmentsException {
		if (body.getDisplayName() != null) user.setDisplayName(body.getDisplayName());
		if (body.getUsername() != null) user.setUsername(body.getUsername());
		if (body.getEmail() != null) changeEmail(user, body);
		if (body.getPassword() != null) changePassword(user, body);
		if (body.getAvatar() <= 0) {
			user.setAvatar(attachmentService.getById(body.getAvatar()));
		}

		try {
			userRepository.save(user);
		} catch (DataIntegrityViolationException e) {
			throw new UserCredentialsDuplicateException();
		}

		log.debug("User ({}) edited successfully", user.getUsername());

		return user;
	}

	public void requestDelete(User user, String password) throws UserCredentialsIsInvalidException {
		if (!PasswordHasher.verifyPassword(password, user.getPassword()))
			throw new UserCredentialsIsInvalidException();

		sendEmail(user, EmailConstant.Type.ACCOUNT_DELETE);
		log.debug("User ({}) delete requested successfully", user.getUsername());
	}

	public void confirmDelete(User user, String pathCode) throws OTPsInvalidException, OTPExpiredException {
		OTP OTP = otpService.validate(pathCode);

		userRepository.delete(user);

		log.debug("User ({}) deleted successfully", user.getUsername());

		if (OTP == null) return; // is dev

		otpService.delete(OTP);
	}

	private void changeEmail(User user, UserEditDTO body) {
		user.setEmail(body.getEmail());
		user.addFlag(UserConstant.Flags.AWAITING_CONFIRMATION);

		sendEmail(user, EmailConstant.Type.EMAIL_VERIFY);
	}

	private void changePassword(User user, UserEditDTO body) {
		user.setPassword(PasswordHasher.hashPassword(body.getPassword()));
		user.addFlag(UserConstant.Flags.AWAITING_CONFIRMATION);

		sendEmail(user, EmailConstant.Type.RESET_PASSWORD);
	}

	private void sendEmail(User user, EmailConstant.Type type) {
		String emailType = type.getValue();
		String code = OTPGenerator.generateDigitCode();
		long issuedAt = System.currentTimeMillis();
		long expiresAt = issuedAt + OTPConstant.Lifetime.BASE.getValue();

		emailService.send(user.getEmail(), user.getId(), emailType, user.getUsername(), code, issuedAt, expiresAt, null);
	}
}
