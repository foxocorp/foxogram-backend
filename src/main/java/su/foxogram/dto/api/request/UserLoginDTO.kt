package su.foxogram.dto.api.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import lombok.Getter
import lombok.Setter
import su.foxogram.constant.ValidationConstant

@Setter
@Getter
@Schema(name = "UserLogin")
data class UserLoginDTO(
    val email: @NotNull(message = "Email" + ValidationConstant.Messages.MUST_NOT_BE_NULL) @Size(
        min = ValidationConstant.Lengths.MIN,
        max = ValidationConstant.Lengths.EMAIL,
        message = ValidationConstant.Messages.EMAIL_WRONG_LENGTH
    ) @Pattern(
        regexp = ValidationConstant.Regex.EMAIL_REGEX,
        message = ValidationConstant.Messages.EMAIL_INCORRECT
    ) String? = null,

    val password: @NotNull(message = "Password" + ValidationConstant.Messages.MUST_NOT_BE_NULL) @Size(
        min = ValidationConstant.Lengths.MIN,
        max = ValidationConstant.Lengths.PASSWORD,
        message = ValidationConstant.Messages.PASSWORD_WRONG_LENGTH
    ) String? = null
)
