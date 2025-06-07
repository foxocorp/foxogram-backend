package su.foxogram.dto.api.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size
import lombok.Getter
import lombok.Setter
import su.foxogram.constant.ValidationConstant

@Setter
@Getter
@Schema(name = "MessageCreate")
data class MessageCreateDTO(
    val content: @Size(
        max = ValidationConstant.Lengths.MESSAGE_CONTENT,
        message = ValidationConstant.Messages.MESSAGE_WRONG_LENGTH
    ) String? = null,

    val attachments: @Size(
        max = ValidationConstant.Lengths.ATTACHMENTS_MAX,
        message = ValidationConstant.Messages.ATTACHMENTS_WRONG_SIZE
    ) MutableList<Long?>? = null
)
