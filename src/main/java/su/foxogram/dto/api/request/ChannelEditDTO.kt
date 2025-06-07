package su.foxogram.dto.api.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import lombok.Getter
import lombok.Setter
import su.foxogram.constant.ValidationConstant

@Getter
@Setter
@Schema(name = "ChannelEdit")
data class ChannelEditDTO(
    val displayName: @Size(
        min = 1,
        max = ValidationConstant.Lengths.CHANNEL_NAME,
        message = ValidationConstant.Messages.CHANNEL_NAME_WRONG_LENGTH
    ) String? = null,

    val name: @Pattern(
        regexp = ValidationConstant.Regex.NAME_REGEX,
        message = ValidationConstant.Messages.CHANNEL_NAME_INCORRECT
    ) @Size(
        min = 1,
        max = ValidationConstant.Lengths.CHANNEL_NAME,
        message = ValidationConstant.Messages.CHANNEL_NAME_WRONG_LENGTH
    ) String? = null,

    val icon: Long = 0
)
