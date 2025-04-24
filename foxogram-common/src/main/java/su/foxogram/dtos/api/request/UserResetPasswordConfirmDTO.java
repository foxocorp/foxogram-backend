package su.foxogram.dtos.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.constants.ValidationConstants;

@Getter
@Setter
@Schema(name = "UserResetPasswordConfirm")
public class UserResetPasswordConfirmDTO {
	@NotNull(message = "Email" + ValidationConstants.Messages.MUST_NOT_BE_NULL)
	@Size(min = ValidationConstants.Lengths.MIN, max = ValidationConstants.Lengths.EMAIL, message = ValidationConstants.Messages.EMAIL_WRONG_LENGTH)
	@Pattern(regexp = ValidationConstants.Regex.EMAIL_REGEX, message = ValidationConstants.Messages.EMAIL_INCORRECT)
	private String email;

	@NotNull(message = "OTP" + ValidationConstants.Messages.MUST_NOT_BE_NULL)
	@Size(min = 6, max = 6, message = ValidationConstants.Messages.OTP_NAME_WRONG_LENGTH)
	private String OTP;

	@NotNull(message = "Password" + ValidationConstants.Messages.MUST_NOT_BE_NULL)
	@Size(min = ValidationConstants.Lengths.MIN, max = ValidationConstants.Lengths.PASSWORD, message = ValidationConstants.Messages.PASSWORD_WRONG_LENGTH)
	private String newPassword;
}
