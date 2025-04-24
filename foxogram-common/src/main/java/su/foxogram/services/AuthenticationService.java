package su.foxogram.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import su.foxogram.configs.APIConfig;
import su.foxogram.constants.EmailConstants;
import su.foxogram.constants.OTPConstants;
import su.foxogram.constants.UserConstants;
import su.foxogram.dtos.api.request.UserResetPasswordConfirmDTO;
import su.foxogram.dtos.api.request.UserResetPasswordDTO;
import su.foxogram.exceptions.otp.NeedToWaitBeforeResendException;
import su.foxogram.exceptions.otp.OTPExpiredException;
import su.foxogram.exceptions.otp.OTPsInvalidException;
import su.foxogram.exceptions.user.UserCredentialsDuplicateException;
import su.foxogram.exceptions.user.UserCredentialsIsInvalidException;
import su.foxogram.exceptions.user.UserEmailNotVerifiedException;
import su.foxogram.exceptions.user.UserUnauthorizedException;
import su.foxogram.models.OTP;
import su.foxogram.models.User;
import su.foxogram.repositories.OTPRepository;
import su.foxogram.repositories.UserRepository;
import su.foxogram.util.OTPGenerator;
import su.foxogram.util.PasswordHasher;

@Slf4j
@Service
public class AuthenticationService {
	private final UserRepository userRepository;

	private final OTPRepository OTPRepository;

	private final EmailService emailService;

	private final JwtService jwtService;

	private final OTPService OTPService;

	private final APIConfig apiConfig;

	@Autowired
	public AuthenticationService(UserRepository userRepository, OTPRepository OTPRepository, EmailService emailService, JwtService jwtService, OTPService OTPService, APIConfig apiConfig) {
		this.userRepository = userRepository;
		this.OTPRepository = OTPRepository;
		this.emailService = emailService;
		this.jwtService = jwtService;
		this.OTPService = OTPService;
		this.apiConfig = apiConfig;
	}

	public User getUser(String header, boolean ignoreEmailVerification, boolean ignoreBearer) throws UserUnauthorizedException, UserEmailNotVerifiedException {
		long userId;
		String passwordHash;

		try {
			String token = header.substring(7);

			if (ignoreBearer) token = header;

			Jws<Claims> claimsJws = Jwts.parser()
					.verifyWith(jwtService.getSigningKey())
					.build()
					.parseSignedClaims(token);

			userId = Long.parseLong(claimsJws.getPayload().getId());
			passwordHash = claimsJws.getPayload().getSubject();
		} catch (Exception e) {
			throw new UserUnauthorizedException();
		}

		User user = userRepository.findById(userId).orElseThrow(UserUnauthorizedException::new);

		if (!user.getPassword().equals(passwordHash)) {
			throw new UserUnauthorizedException();
		}

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

		if (!apiConfig.isDevelopment()) {
			sendConfirmationEmail(user);

			log.info("User ({}, {}) email verification message sent successfully", user.getUsername(), user.getEmail());
		}

		return jwtService.generate(user.getId(), user.getPassword());
	}

	private User createUser(String username, String email, String password) {
		long deletion = 0;
		long flags = UserConstants.Flags.AWAITING_CONFIRMATION.getBit();
		if (apiConfig.isDevelopment()) flags = UserConstants.Flags.EMAIL_VERIFIED.getBit();
		int type = UserConstants.Type.USER.getType();

		return new User(0, null, null, username, email, PasswordHasher.hashPassword(password), flags, type, deletion, null);
	}

	private void sendConfirmationEmail(User user) {
		String emailType = EmailConstants.Type.EMAIL_VERIFY.getValue();
		String digitCode = OTPGenerator.generateDigitCode();
		long issuedAt = System.currentTimeMillis();
		long expiresAt = issuedAt + OTPConstants.Lifetime.BASE.getValue();
		String accessToken = jwtService.generate(user.getId(), user.getPassword());

		emailService.sendEmail(user.getEmail(), user.getId(), emailType, user.getUsername(), digitCode, issuedAt, expiresAt, accessToken);
	}

	public String loginUser(String email, String password) throws UserCredentialsIsInvalidException {
		User user = findUserByEmail(email);
		validatePassword(user, password);

		log.info("User ({}, {}) login successfully", user.getUsername(), user.getEmail());
		return jwtService.generate(user.getId(), user.getPassword());
	}

	public User findUserByEmail(String email) throws UserCredentialsIsInvalidException {
		return userRepository.findByEmail(email).orElseThrow(UserCredentialsIsInvalidException::new);
	}

	private void validatePassword(User user, String password) throws UserCredentialsIsInvalidException {
		if (!PasswordHasher.verifyPassword(password, user.getPassword()))
			throw new UserCredentialsIsInvalidException();
	}

	public void verifyEmail(User user, String pathCode) throws OTPsInvalidException, OTPExpiredException {
		OTP OTP = OTPService.validateCode(pathCode);

		user.removeFlag(UserConstants.Flags.AWAITING_CONFIRMATION);
		user.addFlag(UserConstants.Flags.EMAIL_VERIFIED);
		userRepository.save(user);
		log.info("User ({}, {}) email verified successfully", user.getUsername(), user.getEmail());

		OTPService.delete(OTP);
	}

	public void resendEmail(User user, String accessToken) throws OTPsInvalidException, NeedToWaitBeforeResendException {
		if (apiConfig.isDevelopment()) return;

		OTP OTP = OTPRepository.findByUserId(user.getId());

		if (OTP == null) throw new OTPsInvalidException();

		long issuedAt = OTP.getIssuedAt();
		if (System.currentTimeMillis() - issuedAt < OTPConstants.Lifetime.RESEND.getValue())
			throw new NeedToWaitBeforeResendException();

		log.info("User ({}, {}) email resend successfully", user.getUsername(), user.getEmail());
		emailService.sendEmail(user.getEmail(), user.getId(), OTP.getType(), user.getUsername(), OTP.getValue(), System.currentTimeMillis(), OTP.getExpiresAt(), accessToken);
	}

	public void resetPassword(UserResetPasswordDTO body) throws UserCredentialsIsInvalidException {
		User user = userRepository.findByEmail(body.getEmail()).orElseThrow(UserCredentialsIsInvalidException::new);

		String type = EmailConstants.Type.EMAIL_VERIFY.getValue();
		String value = OTPGenerator.generateDigitCode();
		long issuedAt = System.currentTimeMillis();
		long expiresAt = issuedAt + OTPConstants.Lifetime.BASE.getValue();

		user.addFlag(UserConstants.Flags.AWAITING_CONFIRMATION);

		emailService.sendEmail(user.getEmail(), user.getId(), type, user.getUsername(), value, System.currentTimeMillis(), expiresAt, null);
		log.info("User ({}, {}) reset password requested successfully", user.getUsername(), user.getEmail());
	}

	public void confirmResetPassword(UserResetPasswordConfirmDTO body) throws OTPExpiredException, OTPsInvalidException, UserCredentialsIsInvalidException {
		User user = userRepository.findByEmail(body.getEmail()).orElseThrow(UserCredentialsIsInvalidException::new);
		OTP OTP = OTPService.validateCode(body.getOTP());

		user.setPassword(PasswordHasher.hashPassword(body.getNewPassword()));
		user.removeFlag(UserConstants.Flags.AWAITING_CONFIRMATION);

		OTPService.delete(OTP);
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
