package app.foxochat.dto.api.request;

import app.foxochat.constant.ValidationConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(name = "UserResetPassword")
public class UserResetPasswordDTO {

    @NotNull(message = "Email" + ValidationConstant.Messages.MUST_NOT_BE_NULL)
    @Size(min = ValidationConstant.Lengths.MIN, max = ValidationConstant.Lengths.EMAIL,
            message = ValidationConstant.Messages.EMAIL_WRONG_LENGTH)
    @Pattern(regexp = ValidationConstant.Regex.EMAIL_REGEX, message = ValidationConstant.Messages.EMAIL_INCORRECT)
    private String email;
}
