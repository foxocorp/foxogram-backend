package su.foxogram.dtos.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import su.foxogram.constants.ValidationConstants;

@Getter
@Setter
@Schema(name = "ChannelEdit")
public class ChannelEditDTO {
	@Pattern(regexp = ValidationConstants.Regex.CHANNEL_NAME_REGEX, message = ValidationConstants.Messages.CHANNEL_NAME_INCORRECT)
	@Size(min = 1, max = ValidationConstants.Lengths.CHANNEL_NAME, message = ValidationConstants.Messages.CHANNEL_NAME_WRONG_LENGTH)
	private String displayName;

	@Pattern(regexp = ValidationConstants.Regex.CHANNEL_NAME_REGEX, message = ValidationConstants.Messages.CHANNEL_NAME_INCORRECT)
	@Size(min = 1, max = ValidationConstants.Lengths.CHANNEL_NAME, message = ValidationConstants.Messages.CHANNEL_NAME_WRONG_LENGTH)
	private String name;

	private MultipartFile icon;
}
