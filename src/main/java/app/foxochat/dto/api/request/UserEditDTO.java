package app.foxochat.dto.api.request;

import app.foxochat.constant.ValidationConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "UserEdit")
public class UserEditDTO {

    @Size(min = ValidationConstant.Lengths.MIN, max = ValidationConstant.Lengths.DISPLAY_NAME,
            message = "Display name" + ValidationConstant.Messages.WRONG_LENGTH)
    private String displayName;

    @Size(min = ValidationConstant.Lengths.MIN, max = ValidationConstant.Lengths.USERNAME,
            message = "Username" + ValidationConstant.Messages.WRONG_LENGTH)
    @Pattern(regexp = ValidationConstant.Regex.NAME_REGEX, message = "Username" + ValidationConstant.Messages.INCORRECT)
    private String username;

    @Size(min = ValidationConstant.Lengths.MIN, max = ValidationConstant.Lengths.BIO,
            message = "Bio" + ValidationConstant.Messages.WRONG_LENGTH)
    private String bio;

    @Size(min = ValidationConstant.Lengths.MIN, max = ValidationConstant.Lengths.EMAIL,
            message = "Email" + ValidationConstant.Messages.WRONG_LENGTH)
    @Pattern(regexp = ValidationConstant.Regex.EMAIL_REGEX, message = "Email" + ValidationConstant.Messages.INCORRECT)
    private String email;

    @Size(min = ValidationConstant.Lengths.MIN, max = ValidationConstant.Lengths.PASSWORD,
            message = "Password" + ValidationConstant.Messages.WRONG_LENGTH)
    private String password;

    @PositiveOrZero(message = "Avatar" + ValidationConstant.Messages.MUST_BE_POSITIVE_OR_ZERO)
    private Long avatar;

    @PositiveOrZero(message = "Banner" + ValidationConstant.Messages.MUST_BE_POSITIVE_OR_ZERO)
    private Long banner;
}
