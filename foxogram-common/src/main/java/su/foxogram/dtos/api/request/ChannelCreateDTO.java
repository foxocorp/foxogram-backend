package su.foxogram.dtos.api.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.constants.ValidationConstants;

@Setter
@Getter
public class ChannelCreateDTO {
	@NotNull(message = "Display name" + ValidationConstants.Messages.MUST_NOT_BE_NULL)
	@Pattern(regexp = ValidationConstants.Regex.CHANNEL_NAME_REGEX, message = ValidationConstants.Messages.CHANNEL_NAME_INCORRECT)
	@Size(min = 1, max = ValidationConstants.Lengths.CHANNEL_NAME, message = ValidationConstants.Messages.CHANNEL_NAME_WRONG_LENGTH)
	private String displayName;

	@NotNull(message = "Name" + ValidationConstants.Messages.MUST_NOT_BE_NULL)
	@Pattern(regexp = ValidationConstants.Regex.CHANNEL_NAME_REGEX, message = ValidationConstants.Messages.CHANNEL_NAME_INCORRECT)
	@Size(min = 1, max = ValidationConstants.Lengths.CHANNEL_NAME, message = ValidationConstants.Messages.CHANNEL_NAME_WRONG_LENGTH)
	private String name;

	@NotNull(message = "Type" + ValidationConstants.Messages.MUST_NOT_BE_NULL)
	@Min(value = 1, message = ValidationConstants.Messages.CHANNEL_TYPE_INCORRECT)
	@Max(value = 3, message = ValidationConstants.Messages.CHANNEL_TYPE_INCORRECT)
	private int type;
}
