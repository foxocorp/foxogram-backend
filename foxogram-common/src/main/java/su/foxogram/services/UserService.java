package su.foxogram.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import su.foxogram.configs.APIConfig;
import su.foxogram.constants.EmailConstants;
import su.foxogram.constants.OTPConstants;
import su.foxogram.constants.UserConstants;
import su.foxogram.dtos.api.request.UserEditDTO;
import su.foxogram.exceptions.message.UnknownAttachmentsException;
import su.foxogram.exceptions.otp.OTPExpiredException;
import su.foxogram.exceptions.otp.OTPsInvalidException;
import su.foxogram.exceptions.user.UserCredentialsDuplicateException;
import su.foxogram.exceptions.user.UserCredentialsIsInvalidException;
import su.foxogram.models.*;
import su.foxogram.repositories.UserRepository;
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

	public void updateFlags(User user, UserConstants.Flags removeFlag, UserConstants.Flags addFlag) {
		user.removeFlag(removeFlag);
		user.addFlag(addFlag);
		userRepository.save(user);
	}

	public User add(String username, String email, String password) throws UserCredentialsDuplicateException {
		long deletion = 0;
		long flags = UserConstants.Flags.AWAITING_CONFIRMATION.getBit();
		if (apiConfig.isDevelopment()) flags = UserConstants.Flags.EMAIL_VERIFIED.getBit();
		int type = UserConstants.Type.USER.getType();

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

		sendEmail(user, EmailConstants.Type.ACCOUNT_DELETE);
		log.debug("User ({}) delete requested successfully", user.getUsername());
	}

	public void confirmDelete(User user, String pathCode) throws OTPsInvalidException, OTPExpiredException {
		OTP OTP = otpService.validateCode(pathCode);

		userRepository.delete(user);

		log.debug("User ({}) deleted successfully", user.getUsername());

		if (OTP == null) return; // is dev

		otpService.delete(OTP);
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
