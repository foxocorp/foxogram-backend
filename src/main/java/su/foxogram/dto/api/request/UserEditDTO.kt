package su.foxogram.dto.api.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import lombok.Getter
import lombok.Setter
import su.foxogram.constant.ValidationConstant

@Getter
@Setter
@Schema(name = "UserEdit")
data class UserEditDTO(
    val displayName: @Size(
        min = ValidationConstant.Lengths.MIN,
        max = ValidationConstant.Lengths.DISPLAY_NAME,
        message = ValidationConstant.Messages.DISPLAY_NAME_WRONG_LENGTH
    ) String? = null,

    val username: @Size(
        min = ValidationConstant.Lengths.MIN,
        max = ValidationConstant.Lengths.USERNAME,
        message = ValidationConstant.Messages.USERNAME_WRONG_LENGTH
    ) @Pattern(
        regexp = ValidationConstant.Regex.NAME_REGEX,
        message = ValidationConstant.Messages.USERNAME_INCORRECT
    ) String? = null,

    val email: @Size(
        min = ValidationConstant.Lengths.MIN,
        max = ValidationConstant.Lengths.EMAIL,
        message = ValidationConstant.Messages.EMAIL_WRONG_LENGTH
    ) @Pattern(
        regexp = ValidationConstant.Regex.EMAIL_REGEX,
        message = ValidationConstant.Messages.EMAIL_INCORRECT
    ) String? = null,

    val password: @Size(
        min = ValidationConstant.Lengths.MIN,
        max = ValidationConstant.Lengths.PASSWORD,
        message = ValidationConstant.Messages.PASSWORD_WRONG_LENGTH
    ) String? = null,

    val avatar: @Positive(message = ValidationConstant.Messages.USER_AVATAR_MUST_BE_POSITIVE) Long? = null
)
