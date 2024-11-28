package su.foxogram.dtos.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.constants.ValidationConstants;

@Getter
@Setter
public class UserEditDTO {
    @Size(min = ValidationConstants.Lengths.MIN, max = ValidationConstants.Lengths.USERNAME, message = ValidationConstants.Messages.USERNAME_WRONG_LENGTH)
    @Pattern(regexp = ValidationConstants.Regex.USERNAME_REGEX, message = ValidationConstants.Messages.USERNAME_INCORRECT)
    private String username;

    @Size(min = ValidationConstants.Lengths.MIN, max = ValidationConstants.Lengths.EMAIL, message = ValidationConstants.Messages.EMAIL_WRONG_LENGTH)
    @Pattern(regexp = ValidationConstants.Regex.EMAIL_REGEX, message = ValidationConstants.Messages.EMAIL_INCORRECT)
    private String email;

    private String avatar;
}