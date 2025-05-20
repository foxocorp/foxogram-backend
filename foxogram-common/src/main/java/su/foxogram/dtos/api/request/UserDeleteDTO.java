package su.foxogram.dtos.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.constants.ValidationConstants;

@Setter
@Getter
@Schema(name = "UserDelete")
public class UserDeleteDTO {

	@NotNull(message = "Password" + ValidationConstants.Messages.MUST_NOT_BE_NULL)
	@Size(min = ValidationConstants.Lengths.MIN, max = ValidationConstants.Lengths.PASSWORD, message = ValidationConstants.Messages.PASSWORD_WRONG_LENGTH)
	private String password;
}
