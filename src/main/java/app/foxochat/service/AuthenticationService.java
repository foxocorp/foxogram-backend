package app.foxochat.service;

import app.foxochat.dto.api.request.UserResetPasswordConfirmDTO;
import app.foxochat.dto.api.request.UserResetPasswordDTO;
import app.foxochat.exception.otp.NeedToWaitBeforeResendException;
import app.foxochat.exception.otp.OTPExpiredException;
import app.foxochat.exception.otp.OTPsInvalidException;
import app.foxochat.exception.user.UserCredentialsDuplicateException;
import app.foxochat.exception.user.UserCredentialsIsInvalidException;
import app.foxochat.exception.user.UserEmailNotVerifiedException;
import app.foxochat.exception.user.UserUnauthorizedException;
import app.foxochat.model.User;

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
