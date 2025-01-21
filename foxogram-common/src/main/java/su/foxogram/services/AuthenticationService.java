package su.foxogram.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import su.foxogram.configs.APIConfig;
import su.foxogram.constants.CodesConstants;
import su.foxogram.constants.EmailConstants;
import su.foxogram.constants.UserConstants;
import su.foxogram.dtos.api.request.UserResetPasswordConfirmDTO;
import su.foxogram.dtos.api.request.UserResetPasswordDTO;
import su.foxogram.exceptions.code.CodeExpiredException;
import su.foxogram.exceptions.code.CodeIsInvalidException;
import su.foxogram.exceptions.code.NeedToWaitBeforeResendException;
import su.foxogram.exceptions.user.UserCredentialsDuplicateException;
import su.foxogram.exceptions.user.UserCredentialsIsInvalidException;
import su.foxogram.exceptions.user.UserEmailNotVerifiedException;
import su.foxogram.exceptions.user.UserUnauthorizedException;
import su.foxogram.models.Code;
import su.foxogram.models.User;
import su.foxogram.repositories.CodeRepository;
import su.foxogram.repositories.UserRepository;
import su.foxogram.util.CodeGenerator;
import su.foxogram.util.Encryptor;

@Slf4j
@Service
public class AuthenticationService {
	private final UserRepository userRepository;

	private final CodeRepository codeRepository;

	private final EmailService emailService;

	private final JwtService jwtService;

	private final CodeService codeService;

	private final APIConfig apiConfig;

	@Autowired
	public AuthenticationService(UserRepository userRepository, CodeRepository codeRepository, EmailService emailService, JwtService jwtService, CodeService codeService, APIConfig apiConfig) {
		this.userRepository = userRepository;
		this.codeRepository = codeRepository;
		this.emailService = emailService;
		this.jwtService = jwtService;
		this.codeService = codeService;
		this.apiConfig = apiConfig;
	}

	public User getUser(String header, boolean ignoreEmailVerification, boolean ignoreBearer) throws UserUnauthorizedException, UserEmailNotVerifiedException {
		long userId;

		try {
			String claims = header.substring(7);

			if (ignoreBearer) claims = header;

			Jws<Claims> claimsJws = Jwts.parserBuilder()
					.setSigningKey(jwtService.getSigningKey())
					.build()
					.parseClaimsJws(claims);

			userId = Long.parseLong(claimsJws.getBody().getId());
		} catch (Exception e) {
			throw new UserUnauthorizedException();
		}

		User user = userRepository.findById(userId).orElseThrow(UserUnauthorizedException::new);

		if (!ignoreEmailVerification && user.hasFlag(UserConstants.Flags.EMAIL_VERIFIED))
			throw new UserEmailNotVerifiedException();

		return userRepository.findById(userId).orElseThrow(UserUnauthorizedException::new);
	}

	public String userRegister(String username, String email, String password) throws UserCredentialsDuplicateException {
		User user = createUser(username, email, password);
		try {
			userRepository.save(user);
		} catch (DataIntegrityViolationException e) {
			throw new UserCredentialsDuplicateException();
		}

		log.info("User ({}, {}) created successfully", user.getUsername(), user.getEmail());

		sendConfirmationEmail(user);

		log.info("User ({}, {}) email verification message sent successfully", user.getUsername(), user.getEmail());

		return jwtService.generate(user.getId());
	}

	private User createUser(String username, String email, String password) {
		long deletion = 0;
		long flags = UserConstants.Flags.AWAITING_CONFIRMATION.getBit();
		int type = UserConstants.Type.USER.getType();

		return new User(0, null, null, username, email, Encryptor.hashPassword(password), flags, type, deletion, null);
	}

	private void sendConfirmationEmail(User user) {
		String emailType = EmailConstants.Type.EMAIL_VERIFY.getValue();
		String digitCode = CodeGenerator.generateDigitCode();
		long issuedAt = System.currentTimeMillis();
		long expiresAt = issuedAt + CodesConstants.Lifetime.BASE.getValue();
		String accessToken = jwtService.generate(user.getId());

		emailService.sendEmail(user.getEmail(), user.getId(), emailType, user.getUsername(), digitCode, issuedAt, expiresAt, accessToken);
	}

	public String loginUser(String email, String password) throws UserCredentialsIsInvalidException {
		User user = findUserByEmail(email);
		validatePassword(user, password);

		log.info("User ({}, {}) login successfully", user.getUsername(), user.getEmail());
		return jwtService.generate(user.getId());
	}

	public User findUserByEmail(String email) throws UserCredentialsIsInvalidException {
		return userRepository.findByEmail(email).orElseThrow(UserCredentialsIsInvalidException::new);
	}

	private void validatePassword(User user, String password) throws UserCredentialsIsInvalidException {
		if (!Encryptor.verifyPassword(password, user.getPassword()))
			throw new UserCredentialsIsInvalidException();
	}

	public void verifyEmail(User user, String pathCode) throws CodeIsInvalidException, CodeExpiredException {
		Code code = codeService.validateCode(pathCode);

		user.removeFlag(UserConstants.Flags.AWAITING_CONFIRMATION);
		user.addFlag(UserConstants.Flags.EMAIL_VERIFIED);
		userRepository.save(user);
		log.info("User ({}, {}) email verified successfully", user.getUsername(), user.getEmail());

		if (code == null) return; // is dev

		codeService.deleteCode(code);
	}

	public void resendEmail(User user, String accessToken) throws CodeIsInvalidException, NeedToWaitBeforeResendException {
		if (apiConfig.isDevelopment()) return;

		Code code = codeRepository.findByUserId(user.getId());

		if (code == null) throw new CodeIsInvalidException();

		long issuedAt = code.getIssuedAt();
		if (System.currentTimeMillis() - issuedAt < CodesConstants.Lifetime.RESEND.getValue())
			throw new NeedToWaitBeforeResendException();

		log.info("User ({}, {}) email resend successfully", user.getUsername(), user.getEmail());
		emailService.sendEmail(user.getEmail(), user.getId(), code.getType(), user.getUsername(), code.getValue(), System.currentTimeMillis(), code.getExpiresAt(), accessToken);
	}

	public void resetPassword(UserResetPasswordDTO body) throws UserCredentialsIsInvalidException {
		User user = userRepository.findByEmail(body.getEmail()).orElseThrow(UserCredentialsIsInvalidException::new);

		String type = EmailConstants.Type.EMAIL_VERIFY.getValue();
		String value = CodeGenerator.generateDigitCode();
		long issuedAt = System.currentTimeMillis();
		long expiresAt = issuedAt + CodesConstants.Lifetime.BASE.getValue();

		user.addFlag(UserConstants.Flags.AWAITING_CONFIRMATION);

		emailService.sendEmail(user.getEmail(), user.getId(), type, user.getUsername(), value, System.currentTimeMillis(), expiresAt, null);
		log.info("User ({}, {}) reset password requested successfully", user.getUsername(), user.getEmail());
	}

	public void confirmResetPassword(UserResetPasswordConfirmDTO body) throws CodeExpiredException, CodeIsInvalidException, UserCredentialsIsInvalidException {
		User user = userRepository.findByEmail(body.getEmail()).orElseThrow(UserCredentialsIsInvalidException::new);
		Code code = codeService.validateCode(body.getCode());

		user.setPassword(Encryptor.hashPassword(body.getNewPassword()));
		user.removeFlag(UserConstants.Flags.AWAITING_CONFIRMATION);

		codeService.deleteCode(code);
		log.info("User ({}, {}) password reset successfully", user.getUsername(), user.getEmail());
	}

	public User authUser(String accessToken, boolean ignoreEmailVerification, boolean ignoreBearer) throws UserUnauthorizedException, UserEmailNotVerifiedException {
		if (accessToken == null)
			throw new UserUnauthorizedException();

		if (accessToken.startsWith("Bearer ") && ignoreBearer)
			throw new UserUnauthorizedException();

		return getUser(accessToken, ignoreEmailVerification, ignoreBearer);
	}
}
