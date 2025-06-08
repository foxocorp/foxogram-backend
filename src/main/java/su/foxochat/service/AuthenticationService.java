package su.foxochat.service;

import su.foxochat.dto.api.request.UserResetPasswordConfirmDTO;
import su.foxochat.dto.api.request.UserResetPasswordDTO;
import su.foxochat.exception.otp.NeedToWaitBeforeResendException;
import su.foxochat.exception.otp.OTPExpiredException;
import su.foxochat.exception.otp.OTPsInvalidException;
import su.foxochat.exception.user.UserCredentialsDuplicateException;
import su.foxochat.exception.user.UserCredentialsIsInvalidException;
import su.foxochat.exception.user.UserEmailNotVerifiedException;
import su.foxochat.exception.user.UserUnauthorizedException;
import su.foxochat.model.User;

public interface AuthenticationService {

	User getUser(String token, boolean ignoreEmailVerification, boolean removeBearerFromString) throws UserUnauthorizedException, UserEmailNotVerifiedException;

	String register(String username, String email, String password) throws UserCredentialsDuplicateException;

	String login(String email, String password) throws UserCredentialsIsInvalidException;

	void verifyEmail(User user, String pathCode) throws OTPsInvalidException, OTPExpiredException;

	void resendEmail(User user, String accessToken) throws OTPsInvalidException, NeedToWaitBeforeResendException;

	void resetPassword(UserResetPasswordDTO body) throws UserCredentialsIsInvalidException;

	void confirmResetPassword(UserResetPasswordConfirmDTO body) throws OTPExpiredException, OTPsInvalidException, UserCredentialsIsInvalidException;

	User authUser(String accessToken, boolean ignoreEmailVerification) throws UserUnauthorizedException, UserEmailNotVerifiedException;
}
