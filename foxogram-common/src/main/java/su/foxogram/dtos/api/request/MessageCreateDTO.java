package su.foxogram.dtos.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import su.foxogram.constants.ValidationConstants;

import java.util.List;

@Setter
@Getter
@Schema(name = "MessageCreate")
public class MessageCreateDTO {
	@Size(max = ValidationConstants.Lengths.MESSAGE_CONTENT, message = ValidationConstants.Messages.MESSAGE_WRONG_LENGTH)
	private String content;

	private List<MultipartFile> attachments;
}
