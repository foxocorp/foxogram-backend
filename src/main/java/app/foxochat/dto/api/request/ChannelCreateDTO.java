package app.foxochat.dto.api.request;

import app.foxochat.constant.ValidationConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(name = "ChannelCreate")
public class ChannelCreateDTO {

    @NotNull(message = "Display name" + ValidationConstant.Messages.MUST_NOT_BE_NULL)
    @Size(min = 1, max = ValidationConstant.Lengths.CHANNEL_NAME,
            message = "Display name" + ValidationConstant.Messages.WRONG_LENGTH)
    private String displayName;

    @NotNull(message = "Name" + ValidationConstant.Messages.MUST_NOT_BE_NULL)
    @Pattern(regexp = ValidationConstant.Regex.NAME_REGEX, message = "Name" + ValidationConstant.Messages.INCORRECT)
    @Size(min = 1, max = ValidationConstant.Lengths.CHANNEL_NAME,
            message = "Name" + ValidationConstant.Messages.WRONG_LENGTH)
    private String name;

    @NotNull(message = "Type" + ValidationConstant.Messages.MUST_NOT_BE_NULL)
    @Min(value = 1, message = "Type" + ValidationConstant.Messages.INCORRECT)
    @Max(value = 3, message = "Type" + ValidationConstant.Messages.INCORRECT)
    private int type;

    @NotNull(message = "Is public" + ValidationConstant.Messages.MUST_NOT_BE_NULL)
    private boolean isPublic;
}
