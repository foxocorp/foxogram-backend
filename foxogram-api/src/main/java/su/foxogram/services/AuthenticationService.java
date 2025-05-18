package su.foxogram.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
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

		if (!ignoreEmailVerification && user.hasFlag(UserConstants.Flags.EMAIL_VERIFIED))
			throw new UserEmailNotVerifiedException();

		return userService.getById(userId).orElseThrow(UserUnauthorizedException::new);
	}

	public String userRegister(String username, String email, String password) throws UserCredentialsDuplicateException {
		User user = userService.add(username, email, password);

		log.debug("User ({}) created successfully", user.getUsername());

		if (!apiConfig.isDevelopment()) {
			sendConfirmationEmail(user);

			log.debug("User ({}) email verification message sent successfully", user.getUsername());
		}

		return jwtService.generate(user.getId(), user.getPassword());
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
		User user = userService.getByEmail(email).orElseThrow(UserCredentialsIsInvalidException::new);
		if (!PasswordHasher.verifyPassword(password, user.getPassword()))
			throw new UserCredentialsIsInvalidException();

		log.debug("User ({}) login successfully", user.getUsername());
		return jwtService.generate(user.getId(), user.getPassword());
	}

	public void verifyEmail(User user, String pathCode) throws OTPsInvalidException, OTPExpiredException {
		OTP OTP = otpService.validateCode(pathCode);

		userService.updateFlags(user, UserConstants.Flags.AWAITING_CONFIRMATION, UserConstants.Flags.EMAIL_VERIFIED);
		log.debug("User ({}) email verified successfully", user.getUsername());

		otpService.delete(OTP);
	}

	public void resendEmail(User user, String accessToken) throws OTPsInvalidException, NeedToWaitBeforeResendException {
		if (apiConfig.isDevelopment()) return;

		OTP OTP = otpService.getByUserId(user.getId());

		if (OTP == null) throw new OTPsInvalidException();

		long issuedAt = OTP.getIssuedAt();
		if (System.currentTimeMillis() - issuedAt < OTPConstants.Lifetime.RESEND.getValue())
			throw new NeedToWaitBeforeResendException();

		log.debug("User ({}) email resend successfully", user.getUsername());
		emailService.sendEmail(user.getEmail(), user.getId(), OTP.getType(), user.getUsername(), OTP.getValue(), System.currentTimeMillis(), OTP.getExpiresAt(), accessToken);
	}

	public void resetPassword(UserResetPasswordDTO body) throws UserCredentialsIsInvalidException {
		User user = userService.getByEmail(body.getEmail()).orElseThrow(UserCredentialsIsInvalidException::new);

		String type = EmailConstants.Type.EMAIL_VERIFY.getValue();
		String value = OTPGenerator.generateDigitCode();
		long issuedAt = System.currentTimeMillis();
		long expiresAt = issuedAt + OTPConstants.Lifetime.BASE.getValue();

		user.addFlag(UserConstants.Flags.AWAITING_CONFIRMATION);

		emailService.sendEmail(user.getEmail(), user.getId(), type, user.getUsername(), value, System.currentTimeMillis(), expiresAt, null);
		log.debug("User ({}) reset password requested successfully", user.getUsername());
	}

	public void confirmResetPassword(UserResetPasswordConfirmDTO body) throws OTPExpiredException, OTPsInvalidException, UserCredentialsIsInvalidException {
		User user = userService.getByEmail(body.getEmail()).orElseThrow(UserCredentialsIsInvalidException::new);
		OTP OTP = otpService.validateCode(body.getOTP());

		user.setPassword(PasswordHasher.hashPassword(body.getNewPassword()));
		user.removeFlag(UserConstants.Flags.AWAITING_CONFIRMATION);

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
