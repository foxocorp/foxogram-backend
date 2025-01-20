package su.foxogram.dtos.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import su.foxogram.constants.ValidationConstants;

@Getter
@Setter
@Schema(name = "UserEdit")
public class UserEditDTO {
	@Size(min = ValidationConstants.Lengths.MIN, max = ValidationConstants.Lengths.DISPLAY_NAME, message = ValidationConstants.Messages.DISPLAY_NAME_WRONG_LENGTH)
	private String displayName;

	@Size(min = ValidationConstants.Lengths.MIN, max = ValidationConstants.Lengths.USERNAME, message = ValidationConstants.Messages.USERNAME_WRONG_LENGTH)
	@Pattern(regexp = ValidationConstants.Regex.NAME_REGEX, message = ValidationConstants.Messages.USERNAME_INCORRECT)
	private String username;

	@Size(min = ValidationConstants.Lengths.MIN, max = ValidationConstants.Lengths.EMAIL, message = ValidationConstants.Messages.EMAIL_WRONG_LENGTH)
	@Pattern(regexp = ValidationConstants.Regex.EMAIL_REGEX, message = ValidationConstants.Messages.EMAIL_INCORRECT)
	private String email;

	@Size(min = ValidationConstants.Lengths.MIN, max = ValidationConstants.Lengths.PASSWORD, message = ValidationConstants.Messages.PASSWORD_WRONG_LENGTH)
	private String password;

	private MultipartFile avatar;
}
