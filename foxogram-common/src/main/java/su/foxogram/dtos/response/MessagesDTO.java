package su.foxogram.dtos.response;

import lombok.Getter;
import lombok.Setter;
import su.foxogram.models.Message;

import java.util.List;

@Getter
@Setter
public class MessagesDTO {
	private List<MessageDTO> messages;

	public MessagesDTO(List<Message> messages) {
		for (Message message : messages) {
			this.messages.add(new MessageDTO(message));
		}
	}
}
