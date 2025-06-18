package app.foxochat.dto.api.request;

import app.foxochat.constant.ValidationConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Schema(name = "MessageCreate")
public class MessageCreateDTO {

	@Size(max = ValidationConstant.Lengths.MESSAGE_CONTENT, message = ValidationConstant.Messages.MESSAGE_WRONG_LENGTH)
	private String content;

	@Size(max = ValidationConstant.Lengths.ATTACHMENTS_MAX, message = ValidationConstant.Messages.ATTACHMENTS_WRONG_SIZE)
	private List<Long> attachments;
}
