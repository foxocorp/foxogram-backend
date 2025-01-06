package su.foxogram.dtos.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import su.foxogram.constants.ValidationConstants;

import java.util.List;

@Setter
@Getter
public class MessageCreateDTO {
	@NotNull(message = "Content" + ValidationConstants.Messages.MUST_NOT_BE_NULL)
	@Size(min = 1, max = ValidationConstants.Lengths.MESSAGE_CONTENT, message = ValidationConstants.Messages.MESSAGE_WRONG_LENGTH)
	private String content;

	@NotNull(message = "Attachments" + ValidationConstants.Messages.MUST_NOT_BE_NULL)
	private List<MultipartFile> attachments;
}
