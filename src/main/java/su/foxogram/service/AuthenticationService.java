package su.foxogram.service;

import su.foxogram.dto.api.request.UserResetPasswordConfirmDTO;
import su.foxogram.dto.api.request.UserResetPasswordDTO;
import su.foxogram.exception.otp.NeedToWaitBeforeResendException;
import su.foxogram.exception.otp.OTPExpiredException;
import su.foxogram.exception.otp.OTPsInvalidException;
import su.foxogram.exception.user.UserCredentialsDuplicateException;
import su.foxogram.exception.user.UserCredentialsIsInvalidException;
import su.foxogram.exception.user.UserEmailNotVerifiedException;
import su.foxogram.exception.user.UserUnauthorizedException;
import su.foxogram.model.User;

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
