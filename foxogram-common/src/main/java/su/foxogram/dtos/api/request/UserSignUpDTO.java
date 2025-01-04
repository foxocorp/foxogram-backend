package su.foxogram.dtos.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.constants.ValidationConstants;

@Setter
@Getter
public class UserSignUpDTO {
	@NotNull(message = "Username" + ValidationConstants.Messages.MUST_NOT_BE_NULL)
	@Size(min = ValidationConstants.Lengths.MIN, max = ValidationConstants.Lengths.USERNAME, message = ValidationConstants.Messages.USERNAME_WRONG_LENGTH)
	@Pattern(regexp = ValidationConstants.Regex.USERNAME_REGEX, message = ValidationConstants.Messages.USERNAME_INCORRECT)
	private String username;

	@NotNull(message = "Email" + ValidationConstants.Messages.MUST_NOT_BE_NULL)
	@Size(min = ValidationConstants.Lengths.MIN, max = ValidationConstants.Lengths.EMAIL, message = ValidationConstants.Messages.EMAIL_WRONG_LENGTH)
	@Pattern(regexp = ValidationConstants.Regex.EMAIL_REGEX, message = ValidationConstants.Messages.EMAIL_INCORRECT)
	private String email;

	@NotNull(message = "Password" + ValidationConstants.Messages.MUST_NOT_BE_NULL)
	@Size(min = ValidationConstants.Lengths.MIN, max = ValidationConstants.Lengths.PASSWORD, message = ValidationConstants.Messages.PASSWORD_WRONG_LENGTH)
	private String password;
}
