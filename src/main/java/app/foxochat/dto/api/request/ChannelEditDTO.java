package app.foxochat.dto.api.request;

import app.foxochat.constant.ValidationConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "ChannelEdit")
public class ChannelEditDTO {

    @Size(min = 1, max = ValidationConstant.Lengths.CHANNEL_NAME,
            message = "Display name" + ValidationConstant.Messages.WRONG_LENGTH)
    private String displayName;

    @Pattern(regexp = ValidationConstant.Regex.NAME_REGEX, message = "Name" + ValidationConstant.Messages.INCORRECT)
    @Size(min = 1, max = ValidationConstant.Lengths.CHANNEL_NAME,
            message = "Name" + ValidationConstant.Messages.WRONG_LENGTH)
    private String name;

    @PositiveOrZero(message = "Avatar" + ValidationConstant.Messages.MUST_BE_POSITIVE_OR_ZERO)
    private Long avatar;

    @PositiveOrZero(message = "Banner" + ValidationConstant.Messages.MUST_BE_POSITIVE_OR_ZERO)
    private Long banner;

    @NotNull(message = "isPublic" + ValidationConstant.Messages.MUST_NOT_BE_NULL)
    private Boolean isPublic;
}
