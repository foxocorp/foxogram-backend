package su.foxogram.dtos.api.response;

import lombok.Getter;
import lombok.Setter;
import su.foxogram.models.Message;

import java.util.List;

@Getter
@Setter
public class MessageDTO {

	private long id;

	private String content;

	private long author;

	private String channel;

	private List<String> attachments;

	public MessageDTO(Message message) {
		this.id = message.getId();
		this.content = message.getContent();
		this.author = message.getAuthor().getId();
		this.channel = message.getChannel().getName();
		this.attachments = message.getAttachments();
	}
}
