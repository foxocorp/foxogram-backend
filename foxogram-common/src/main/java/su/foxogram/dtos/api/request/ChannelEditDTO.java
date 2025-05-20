package su.foxogram.dtos.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.constants.ValidationConstants;

@Getter
@Setter
@Schema(name = "ChannelEdit")
public class ChannelEditDTO {

	@Size(min = 1, max = ValidationConstants.Lengths.CHANNEL_NAME, message = ValidationConstants.Messages.CHANNEL_NAME_WRONG_LENGTH)
	private String displayName;

	@Pattern(regexp = ValidationConstants.Regex.NAME_REGEX, message = ValidationConstants.Messages.CHANNEL_NAME_INCORRECT)
	@Size(min = 1, max = ValidationConstants.Lengths.CHANNEL_NAME, message = ValidationConstants.Messages.CHANNEL_NAME_WRONG_LENGTH)
	private String name;

	private long icon;
}
