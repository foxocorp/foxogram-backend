package app.foxochat.dto.api.request;

import app.foxochat.constant.ValidationConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "ChannelEdit")
public class ChannelEditDTO {

    @Size(min = 1, max = ValidationConstant.Lengths.CHANNEL_NAME,
            message = ValidationConstant.Messages.CHANNEL_NAME_WRONG_LENGTH)
    private String displayName;

    @Pattern(regexp = ValidationConstant.Regex.NAME_REGEX, message = ValidationConstant.Messages.CHANNEL_NAME_INCORRECT)
    @Size(min = 1, max = ValidationConstant.Lengths.CHANNEL_NAME,
            message = ValidationConstant.Messages.CHANNEL_NAME_WRONG_LENGTH)
    private String name;

    @Positive(message = ValidationConstant.Messages.CHANNEL_ICON_MUST_BE_POSITIVE)
    private Long icon;
}
