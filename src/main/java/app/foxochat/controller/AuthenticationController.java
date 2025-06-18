package app.foxochat.controller;

import app.foxochat.constant.APIConstant;
import app.foxochat.constant.AttributeConstant;
import app.foxochat.dto.api.request.*;
import app.foxochat.dto.api.response.OkDTO;
import app.foxochat.dto.api.response.TokenDTO;
import app.foxochat.exception.otp.NeedToWaitBeforeResendException;
import app.foxochat.exception.otp.OTPExpiredException;
import app.foxochat.exception.otp.OTPsInvalidException;
import app.foxochat.exception.user.UserCredentialsDuplicateException;
import app.foxochat.exception.user.UserCredentialsIsInvalidException;
import app.foxochat.model.User;
import app.foxochat.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Tag(name = "Authentication")
@RequestMapping(value = APIConstant.AUTH, produces = "application/json")
public class AuthenticationController {

	private final AuthenticationService authenticationService;

	public AuthenticationController(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@Operation(summary = "Register")
	@SecurityRequirements
	@PostMapping("/register")
	public TokenDTO register(@RequestBody UserRegisterDTO body) throws UserCredentialsDuplicateException {
		String username = body.getUsername();
		String email = body.getEmail();
		String password = body.getPassword();
		String accessToken = authenticationService.register(username, email, password);

		return new TokenDTO(accessToken);
	}

	@Operation(summary = "Login")
	@SecurityRequirements
	@PostMapping("/login")
	public TokenDTO login(@RequestBody UserLoginDTO body) throws UserCredentialsIsInvalidException {
		String email = body.getEmail();
		String password = body.getPassword();

		String accessToken = authenticationService.login(email, password);

		return new TokenDTO(accessToken);
	}

	@Operation(summary = "Verify email")
	@PostMapping("/email/verify")
	public OkDTO emailVerify(@RequestAttribute(value = AttributeConstant.USER) User user, @RequestBody OTPDTO body) throws OTPsInvalidException, OTPExpiredException {
		authenticationService.verifyEmail(user, body.getOTP());

		return new OkDTO(true);
	}

	@Operation(summary = "Resend email")
	@PostMapping("/email/resend")
	public OkDTO resendEmail(@RequestAttribute(value = AttributeConstant.USER) User user, @RequestAttribute(value = AttributeConstant.ACCESS_TOKEN) String accessToken) throws OTPsInvalidException, NeedToWaitBeforeResendException {
		authenticationService.resendEmail(user, accessToken);

		return new OkDTO(true);
	}

	@Operation(summary = "Reset password")
	@SecurityRequirements
	@PostMapping("/reset-password")
	public OkDTO resetPassword(@RequestBody UserResetPasswordDTO body) throws UserCredentialsIsInvalidException {
		authenticationService.resetPassword(body);

		return new OkDTO(true);
	}

	@Operation(summary = "Confirm reset password")
	@SecurityRequirements
	@PostMapping("/reset-password/confirm")
	public OkDTO confirmResetPassword(@RequestBody UserResetPasswordConfirmDTO body) throws OTPExpiredException, OTPsInvalidException, UserCredentialsIsInvalidException {
		authenticationService.confirmResetPassword(body);

		return new OkDTO(true);
	}
}
