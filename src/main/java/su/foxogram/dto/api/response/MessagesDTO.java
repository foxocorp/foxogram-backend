package su.foxogram.dto.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.model.Message;

import java.util.List;

@Getter
@Setter
@Schema(name = "Messages")
public class MessagesDTO {

	private List<MessageDTO> messages;

	public MessagesDTO(List<Message> messages) {
		for (Message message : messages) {
			this.messages.add(new MessageDTO(message, true));
		}
	}
}
