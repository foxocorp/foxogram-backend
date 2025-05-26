package su.foxogram.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import su.foxogram.config.APIConfig;
import su.foxogram.constant.EmailConstant;
import su.foxogram.constant.OTPConstant;
import su.foxogram.constant.UserConstant;
import su.foxogram.dto.api.request.UserResetPasswordConfirmDTO;
import su.foxogram.dto.api.request.UserResetPasswordDTO;
import su.foxogram.exception.otp.NeedToWaitBeforeResendException;
import su.foxogram.exception.otp.OTPExpiredException;
import su.foxogram.exception.otp.OTPsInvalidException;
import su.foxogram.exception.user.UserCredentialsDuplicateException;
import su.foxogram.exception.user.UserCredentialsIsInvalidException;
import su.foxogram.exception.user.UserEmailNotVerifiedException;
import su.foxogram.exception.user.UserUnauthorizedException;
import su.foxogram.model.OTP;
import su.foxogram.model.User;
import su.foxogram.util.OTPGenerator;
import su.foxogram.util.PasswordHasher;

@Slf4j
@Service
public class AuthenticationService {

	private final UserService userService;

	private final EmailService emailService;

	private final JwtService jwtService;

	private final OTPService otpService;

	private final APIConfig apiConfig;

	public AuthenticationService(UserService userService, EmailService emailService, JwtService jwtService, OTPService otpService, APIConfig apiConfig) {
		this.userService = userService;
		this.emailService = emailService;
		this.jwtService = jwtService;
		this.otpService = otpService;
		this.apiConfig = apiConfig;
	}

	public User getUser(String token, boolean ignoreEmailVerification) throws UserUnauthorizedException, UserEmailNotVerifiedException {
		token = token.substring(7);

		long userId;
		String passwordHash;

		try {
			Jws<Claims> claimsJws = Jwts.parser()
					.verifyWith(jwtService.getSigningKey())
					.build()
					.parseSignedClaims(token);

			userId = Long.parseLong(claimsJws.getPayload().getId());
			passwordHash = claimsJws.getPayload().getSubject();
		} catch (Exception e) {
			throw new UserUnauthorizedException();
		}

		User user = userService.getById(userId).orElseThrow(UserUnauthorizedException::new);

		if (!user.getPassword().equals(passwordHash)) {
			throw new UserUnauthorizedException();
		}

		if (!ignoreEmailVerification && user.hasFlag(UserConstant.Flags.EMAIL_VERIFIED))
			throw new UserEmailNotVerifiedException();

		return userService.getById(userId).orElseThrow(UserUnauthorizedException::new);
	}

	public String register(String username, String email, String password) throws UserCredentialsDuplicateException {
		User user = userService.add(username, email, password);

		log.debug("User ({}) created successfully", user.getUsername());

		if (!apiConfig.isDevelopment()) {
			sendConfirmationEmail(user);

			log.debug("User ({}) email verification message sent successfully", user.getUsername());
		}

		return jwtService.generate(user.getId(), user.getPassword());
	}

	private void sendConfirmationEmail(User user) {
		String emailType = EmailConstant.Type.EMAIL_VERIFY.getValue();
		String digitCode = OTPGenerator.generateDigitCode();
		long issuedAt = System.currentTimeMillis();
		long expiresAt = issuedAt + OTPConstant.Lifetime.BASE.getValue();
		String accessToken = jwtService.generate(user.getId(), user.getPassword());

		emailService.send(user.getEmail(), user.getId(), emailType, user.getUsername(), digitCode, issuedAt, expiresAt, accessToken);
	}

	public String login(String email, String password) throws UserCredentialsIsInvalidException {
		User user = userService.getByEmail(email).orElseThrow(UserCredentialsIsInvalidException::new);
		if (!PasswordHasher.verifyPassword(password, user.getPassword()))
			throw new UserCredentialsIsInvalidException();

		log.debug("User ({}) login successfully", user.getUsername());
		return jwtService.generate(user.getId(), user.getPassword());
	}

	public void verifyEmail(User user, String pathCode) throws OTPsInvalidException, OTPExpiredException {
		OTP OTP = otpService.validate(pathCode);

		userService.updateFlags(user, UserConstant.Flags.AWAITING_CONFIRMATION, UserConstant.Flags.EMAIL_VERIFIED);
		log.debug("User ({}) email verified successfully", user.getUsername());

		otpService.delete(OTP);
	}

	public void resendEmail(User user, String accessToken) throws OTPsInvalidException, NeedToWaitBeforeResendException {
		if (apiConfig.isDevelopment()) return;

		OTP OTP = otpService.getByUserId(user.getId());

		if (OTP == null) throw new OTPsInvalidException();

		long issuedAt = OTP.getIssuedAt();
		if (System.currentTimeMillis() - issuedAt < OTPConstant.Lifetime.RESEND.getValue())
			throw new NeedToWaitBeforeResendException();

		log.debug("User ({}) email resend successfully", user.getUsername());
		emailService.send(user.getEmail(), user.getId(), OTP.getType(), user.getUsername(), OTP.getValue(), System.currentTimeMillis(), OTP.getExpiresAt(), accessToken);
	}

	public void resetPassword(UserResetPasswordDTO body) throws UserCredentialsIsInvalidException {
		User user = userService.getByEmail(body.getEmail()).orElseThrow(UserCredentialsIsInvalidException::new);

		String type = EmailConstant.Type.EMAIL_VERIFY.getValue();
		String value = OTPGenerator.generateDigitCode();
		long issuedAt = System.currentTimeMillis();
		long expiresAt = issuedAt + OTPConstant.Lifetime.BASE.getValue();

		user.addFlag(UserConstant.Flags.AWAITING_CONFIRMATION);

		emailService.send(user.getEmail(), user.getId(), type, user.getUsername(), value, System.currentTimeMillis(), expiresAt, null);
		log.debug("User ({}) reset password requested successfully", user.getUsername());
	}

	public void confirmResetPassword(UserResetPasswordConfirmDTO body) throws OTPExpiredException, OTPsInvalidException, UserCredentialsIsInvalidException {
		User user = userService.getByEmail(body.getEmail()).orElseThrow(UserCredentialsIsInvalidException::new);
		OTP OTP = otpService.validate(body.getOTP());

		user.setPassword(PasswordHasher.hashPassword(body.getNewPassword()));
		user.removeFlag(UserConstant.Flags.AWAITING_CONFIRMATION);

		otpService.delete(OTP);
		log.debug("User ({}) password reset successfully", user.getUsername());
	}

	public User authUser(String accessToken, boolean ignoreEmailVerification) throws UserUnauthorizedException, UserEmailNotVerifiedException {
		if (accessToken == null)
			throw new UserUnauthorizedException();

		if (!accessToken.startsWith("Bearer "))
			throw new UserUnauthorizedException();

		return getUser(accessToken, ignoreEmailVerification);
	}
}
