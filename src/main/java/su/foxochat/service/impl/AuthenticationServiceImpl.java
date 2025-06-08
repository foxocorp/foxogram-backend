package su.foxochat.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import su.foxochat.config.APIConfig;
import su.foxochat.constant.EmailConstant;
import su.foxochat.constant.OTPConstant;
import su.foxochat.constant.UserConstant;
import su.foxochat.dto.api.request.UserResetPasswordConfirmDTO;
import su.foxochat.dto.api.request.UserResetPasswordDTO;
import su.foxochat.exception.otp.NeedToWaitBeforeResendException;
import su.foxochat.exception.otp.OTPExpiredException;
import su.foxochat.exception.otp.OTPsInvalidException;
import su.foxochat.exception.user.UserCredentialsDuplicateException;
import su.foxochat.exception.user.UserCredentialsIsInvalidException;
import su.foxochat.exception.user.UserEmailNotVerifiedException;
import su.foxochat.exception.user.UserUnauthorizedException;
import su.foxochat.model.OTP;
import su.foxochat.model.User;
import su.foxochat.service.*;
import su.foxochat.util.OTPGenerator;
import su.foxochat.util.PasswordHasher;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	private final UserService userService;

	private final EmailService emailService;

	private final JwtService jwtService;

	private final OTPService otpService;

	private final APIConfig apiConfig;

	private final ObjectMapper objectMapper;

	public AuthenticationServiceImpl(UserService userService, EmailService emailService, JwtService jwtService, OTPService otpService, APIConfig apiConfig, ObjectMapper objectMapper) {
		this.userService = userService;
		this.emailService = emailService;
		this.jwtService = jwtService;
		this.otpService = otpService;
		this.apiConfig = apiConfig;
		this.objectMapper = objectMapper;
	}

	public User getUser(String token, boolean ignoreEmailVerification, boolean removeBearerFromString) throws UserUnauthorizedException, UserEmailNotVerifiedException {
		if (token.startsWith("Bearer ")) {
			token = token.substring(7);
		}

		User user;

		try {
			String[] parts = token.split("\\.");
			String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
			Map<String, Object> claims = objectMapper.readValue(payload, new TypeReference<>() {
			});
			long userId = Long.parseLong((String) claims.get("jti"));

			user = userService.getById(userId).orElseThrow(UserUnauthorizedException::new);

			Jwts.parser().verifyWith(jwtService.getSigningKey(user.getTokenVersion())).build().parseSignedClaims(token);
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

		emailService.send(user.getEmail(), user.getId(), type, user.getUsername(), value, System.currentTimeMillis(), expiresAt, null);
		log.debug("User ({}) reset password requested successfully", user.getUsername());
	}

	public void confirmResetPassword(UserResetPasswordConfirmDTO body) throws OTPExpiredException, OTPsInvalidException, UserCredentialsIsInvalidException {
		User user = userService.getByEmail(body.getEmail()).orElseThrow(UserCredentialsIsInvalidException::new);
		OTP OTP = otpService.validate(body.getOTP());

		user.setPassword(PasswordHasher.hashPassword(body.getNewPassword()));
		user.setTokenVersion(user.getTokenVersion() + 1);
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
