package su.foxogram.dtos.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.models.Attachment;
import su.foxogram.models.Message;

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

	public MessageDTO(Message message, List<Attachment> attachments, boolean includeChannel) {
		this.id = message.getId();
		this.content = message.getContent();
		this.author = new MemberDTO(message.getAuthor(), false);
		if (includeChannel) this.channel = new ChannelDTO(message.getChannel(), null);
		if (attachments != null) {
			this.attachments = attachments.stream()
					.map(attachment -> new AttachmentDTO(
							attachment.getId(),
							attachment.getUuid(),
							attachment.getFilename(),
							attachment.getContentType(),
							attachment.getFlags()
					))
					.collect(Collectors.toList());
		} else this.attachments = null;
		this.createdAt = message.getTimestamp();
	}
}
