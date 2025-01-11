package su.foxogram.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import su.foxogram.constants.APIConstants;
import su.foxogram.constants.AttributesConstants;
import su.foxogram.dtos.api.request.*;
import su.foxogram.dtos.api.response.OkDTO;
import su.foxogram.dtos.api.response.TokenDTO;
import su.foxogram.exceptions.code.CodeExpiredException;
import su.foxogram.exceptions.code.CodeIsInvalidException;
import su.foxogram.exceptions.code.NeedToWaitBeforeResendException;
import su.foxogram.exceptions.user.UserCredentialsDuplicateException;
import su.foxogram.exceptions.user.UserCredentialsIsInvalidException;
import su.foxogram.models.User;
import su.foxogram.services.AuthenticationService;

@Slf4j
@RestController
@Tag(name = "Authentication")
@RequestMapping(value = APIConstants.AUTH, produces = "application/json")
public class AuthenticationController {
	private final AuthenticationService authenticationService;

	@Autowired
	public AuthenticationController(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@Operation(summary = "Register")
	@SecurityRequirements()
	@PostMapping("/register")
	public TokenDTO register(@Valid @RequestBody UserRegisterDTO body) throws UserCredentialsDuplicateException {
		String username = body.getUsername();
		String email = body.getEmail();
		String password = body.getPassword();
		String accessToken = authenticationService.userRegister(username, email, password);

		return new TokenDTO(accessToken);
	}

	@Operation(summary = "Login")
	@SecurityRequirements()
	@PostMapping("/login")
	public TokenDTO login(@Valid @RequestBody UserLoginDTO body) throws UserCredentialsIsInvalidException {
		String email = body.getEmail();
		String password = body.getPassword();

		String accessToken = authenticationService.loginUser(email, password);

		return new TokenDTO(accessToken);
	}

	@Operation(summary = "Verify email")
	@PostMapping("/email/verify")
	public OkDTO emailVerify(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestBody CodeDTO body) throws CodeIsInvalidException, CodeExpiredException {
		authenticationService.verifyEmail(user, body.getCode());

		return new OkDTO(true);
	}

	@Operation(summary = "Resend email")
	@PostMapping("/email/resend")
	public OkDTO resendEmail(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.ACCESS_TOKEN) String accessToken) throws CodeIsInvalidException, NeedToWaitBeforeResendException {
		authenticationService.resendEmail(user, accessToken);

		return new OkDTO(true);
	}

	@Operation(summary = "Reset password")
	@SecurityRequirements()
	@PostMapping("/reset-password")
	public OkDTO resetPassword(@RequestAttribute(value = AttributesConstants.ACCESS_TOKEN) String accessToken, @RequestBody UserResetPasswordDTO body) throws UserCredentialsIsInvalidException {
		authenticationService.resetPassword(accessToken, body);

		return new OkDTO(true);
	}

	@Operation(summary = "Confirm reset password")
	@SecurityRequirements()
	@PostMapping("/reset-password/confirm")
	public OkDTO confirmResetPassword(@RequestAttribute(value = AttributesConstants.ACCESS_TOKEN) String accessToken, @RequestBody UserResetPasswordConfirmDTO body) throws CodeExpiredException, CodeIsInvalidException, UserCredentialsIsInvalidException {
		authenticationService.confirmResetPassword(body);

		return new OkDTO(true);
	}
}
