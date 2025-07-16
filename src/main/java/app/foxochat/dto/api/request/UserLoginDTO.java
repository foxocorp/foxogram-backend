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
@Schema(name = "UserLogin")
public class UserLoginDTO {

    @NotNull(message = "Email/Username" + ValidationConstant.Messages.MUST_NOT_BE_NULL)
    @Size(min = ValidationConstant.Lengths.MIN, max = ValidationConstant.Lengths.EMAIL,
            message = "Email/Username" + ValidationConstant.Messages.WRONG_LENGTH)
    private String identity;

    @NotNull(message = "Password" + ValidationConstant.Messages.MUST_NOT_BE_NULL)
    @Size(min = ValidationConstant.Lengths.MIN, max = ValidationConstant.Lengths.PASSWORD,
            message = "Password" + ValidationConstant.Messages.WRONG_LENGTH)
    private String password;
}
