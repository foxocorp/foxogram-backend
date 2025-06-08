package su.foxochat.dto.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import su.foxochat.model.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Schema(name = "Message")
public class MessageDTO {

	private long id;

	private String content;

	private MemberDTO author;

	private ChannelDTO channel;

	private List<AttachmentDTO> attachments;

	private long createdAt;

	public MessageDTO(Message message, boolean includeChannel) {
		this.id = message.getId();
		this.content = message.getContent();
		this.author = new MemberDTO(message.getAuthor(), false);
		if (includeChannel) this.channel = new ChannelDTO(message.getChannel(), null);
		if (message.getAttachments() != null) this.attachments = message.getAttachments().stream()
					.map(messageAttachment -> new AttachmentDTO(messageAttachment.getAttachment()))
					.collect(Collectors.toList());
		else this.attachments = new ArrayList<>();
		this.createdAt = message.getTimestamp();
	}
}
