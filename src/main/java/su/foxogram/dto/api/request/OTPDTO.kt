package su.foxogram.dto.api.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import lombok.Getter
import lombok.Setter
import su.foxogram.constant.ValidationConstant

@Setter
@Getter
@Schema(name = "OTP")
data class OTPDTO(
    val OTP: @NotNull(message = "OTP" + ValidationConstant.Messages.MUST_NOT_BE_NULL) @Size(
        min = 6,
        max = 6,
        message = ValidationConstant.Messages.OTP_NAME_WRONG_LENGTH
    ) String? = null
)
