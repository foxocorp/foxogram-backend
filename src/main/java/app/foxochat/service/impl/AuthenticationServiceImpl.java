package app.foxochat.service.impl;

import app.foxochat.config.APIConfig;
import app.foxochat.constant.EmailConstant;
import app.foxochat.constant.OTPConstant;
import app.foxochat.constant.UserConstant;
import app.foxochat.dto.api.request.UserResetPasswordConfirmDTO;
import app.foxochat.dto.api.request.UserResetPasswordDTO;
import app.foxochat.exception.otp.NeedToWaitBeforeResendException;
import app.foxochat.exception.otp.OTPExpiredException;
import app.foxochat.exception.otp.OTPsInvalidException;
import app.foxochat.exception.user.UserCredentialsDuplicateException;
import app.foxochat.exception.user.UserCredentialsIsInvalidException;
import app.foxochat.exception.user.UserEmailNotVerifiedException;
import app.foxochat.exception.user.UserUnauthorizedException;
import app.foxochat.model.OTP;
import app.foxochat.model.User;
import app.foxochat.service.*;
import app.foxochat.util.OTPGenerator;
import app.foxochat.util.PasswordHasher;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	private final UserService userService;

	private final EmailService emailService;

	private final JwtService jwtService;

	private final OTPService otpService;

	private final APIConfig apiConfig;

	public AuthenticationServiceImpl(UserService userService, EmailService emailService, JwtService jwtService, OTPService otpService, APIConfig apiConfig) {
		this.userService = userService;
		this.emailService = emailService;
		this.jwtService = jwtService;
		this.otpService = otpService;
		this.apiConfig = apiConfig;
	}

	public User getUser(String token, boolean ignoreEmailVerification, boolean removeBearerFromString) throws UserUnauthorizedException, UserEmailNotVerifiedException {
		if (token.startsWith("Bearer ")) {
			token = token.substring(7);
		}

		User user;

		try {
			Claims claimsJws = Jwts.parser()
					.verifyWith(jwtService.getSigningKey())
					.build()
					.parseSignedClaims(token).getPayload();
			long userId = Long.parseLong(claimsJws.getId());
			long tokenVersion = Long.parseLong(claimsJws.getSubject());

			user = userService.getById(userId).orElseThrow(UserUnauthorizedException::new);

			if (tokenVersion != user.getTokenVersion()) throw new UserUnauthorizedException();
		} catch (Exception e) {
			throw new UserUnauthorizedException();
		}

		if (!ignoreEmailVerification && user.hasFlag(UserConstant.Flags.EMAIL_VERIFIED))
			throw new UserEmailNotVerifiedException();

		return user;
	}

	public String register(String username, String email, String password) throws UserCredentialsDuplicateException {
		User user = userService.add(username, email, password);

		log.debug("User ({}) created successfully", user.getUsername());

		if (!apiConfig.isDevelopment()) {
			sendConfirmationEmail(user);

			log.debug("User ({}) email verification message sent successfully", user.getUsername());
		}

		return jwtService.generate(user);
	}

	private void sendConfirmationEmail(User user) {
		String emailType = EmailConstant.Type.EMAIL_VERIFY.getValue();
		String digitCode = OTPGenerator.generateDigitCode();
		long issuedAt = System.currentTimeMillis();
		long expiresAt = issuedAt + OTPConstant.Lifetime.BASE.getValue();
		String accessToken = jwtService.generate(user);

		emailService.send(user.getEmail(), user.getId(), emailType, user.getUsername(), digitCode, issuedAt, expiresAt, accessToken);
	}

	public String login(String email, String password) throws UserCredentialsIsInvalidException {
		User user = userService.getByEmail(email).orElseThrow(UserCredentialsIsInvalidException::new);
		if (!PasswordHasher.verifyPassword(password, user.getPassword())) throw new UserCredentialsIsInvalidException();

		log.debug("User ({}) login successfully", user.getUsername());
		return jwtService.generate(user);
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
		userService.save(user);

		emailService.send(user.getEmail(), user.getId(), type, user.getUsername(), value, System.currentTimeMillis(), expiresAt, null);
		log.debug("User ({}) reset password requested successfully", user.getUsername());
	}

	public void confirmResetPassword(UserResetPasswordConfirmDTO body) throws OTPExpiredException, OTPsInvalidException, UserCredentialsIsInvalidException {
		User user = userService.getByEmail(body.getEmail()).orElseThrow(UserCredentialsIsInvalidException::new);
		OTP OTP = otpService.validate(body.getOTP());

		user.setPassword(PasswordHasher.hashPassword(body.getNewPassword()));
		user.setTokenVersion(user.getTokenVersion() + 1);
		userService.save(user);
		user.removeFlag(UserConstant.Flags.AWAITING_CONFIRMATION);

		otpService.delete(OTP);
		log.debug("User ({}) password reset successfully", user.getUsername());
	}

	public User authUser(String accessToken, boolean ignoreEmailVerification) throws UserUnauthorizedException, UserEmailNotVerifiedException {
		if (accessToken == null) throw new UserUnauthorizedException();

		if (!accessToken.startsWith("Bearer ")) throw new UserUnauthorizedException();

		return getUser(accessToken, ignoreEmailVerification, true);
	}
}
