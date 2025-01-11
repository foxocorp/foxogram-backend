package su.foxogram.dtos.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.constants.ValidationConstants;

@Setter
@Getter
@Schema(name = "Code")
public class CodeDTO {
	@NotNull(message = "Code" + ValidationConstants.Messages.MUST_NOT_BE_NULL)
	@Size(min = 6, max = 6, message = ValidationConstants.Messages.CODE_NAME_WRONG_LENGTH)
	private String code;
}
