package su.foxogram.dtos.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.models.Message;

import java.util.List;

@Getter
@Setter
@Schema(name = "Message")
public class MessageDTO {

	private long id;

	private String content;

	private long author;

	private long channel;

	private List<String> attachments;

	private long createdAt;

	public MessageDTO(Message message) {
		this.id = message.getId();
		this.content = message.getContent();
		this.author = message.getAuthor().getId();
		this.channel = message.getChannel().getId();
		this.attachments = message.getAttachments();
		this.createdAt = message.getTimestamp();
	}
}
