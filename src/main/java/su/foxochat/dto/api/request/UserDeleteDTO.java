package su.foxochat.dto.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import su.foxochat.constant.ValidationConstant;

@Setter
@Getter
@Schema(name = "UserDelete")
public class UserDeleteDTO {

	@NotNull(message = "Password" + ValidationConstant.Messages.MUST_NOT_BE_NULL)
	@Size(min = ValidationConstant.Lengths.MIN, max = ValidationConstant.Lengths.PASSWORD, message = ValidationConstant.Messages.PASSWORD_WRONG_LENGTH)
	private String password;
}
