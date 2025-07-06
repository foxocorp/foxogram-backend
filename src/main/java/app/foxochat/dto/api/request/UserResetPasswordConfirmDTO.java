package app.foxochat.dto.api.request;

import app.foxochat.constant.ValidationConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "UserResetPasswordConfirm")
public class UserResetPasswordConfirmDTO {

    @NotNull(message = "Email" + ValidationConstant.Messages.MUST_NOT_BE_NULL)
    @Size(min = ValidationConstant.Lengths.MIN, max = ValidationConstant.Lengths.EMAIL,
            message = "Email" + ValidationConstant.Messages.WRONG_LENGTH)
    @Pattern(regexp = ValidationConstant.Regex.EMAIL_REGEX, message = "Email" + ValidationConstant.Messages.INCORRECT)
    private String email;

    @NotNull(message = "OTP" + ValidationConstant.Messages.MUST_NOT_BE_NULL)
    @Size(min = 6, max = 6, message = "OTP" + ValidationConstant.Messages.WRONG_LENGTH)
    private String OTP;

    @NotNull(message = "Password" + ValidationConstant.Messages.MUST_NOT_BE_NULL)
    @Size(min = ValidationConstant.Lengths.MIN, max = ValidationConstant.Lengths.PASSWORD,
            message = "Password" + ValidationConstant.Messages.WRONG_LENGTH)
    private String newPassword;
}
