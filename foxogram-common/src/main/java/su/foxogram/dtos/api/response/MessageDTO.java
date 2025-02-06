package su.foxogram.dtos.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.models.Attachment;
import su.foxogram.models.Message;

import java.util.List;

@Getter
@Setter
@Schema(name = "Message")
public class MessageDTO {

	private long id;

	private String content;

	private MemberDTO author;

	private ChannelDTO channel;

	private List<?> attachments;

	private long createdAt;

	public MessageDTO(Message message, List<Attachment> attachments) {
		this.id = message.getId();
		this.content = message.getContent();
		this.author = new MemberDTO(message.getAuthor(), false);
		this.channel = new ChannelDTO(message.getChannel(), null);
		if (attachments != null) this.attachments = attachments;
		else this.attachments = message.getAttachments();
		this.createdAt = message.getTimestamp();
	}
}
