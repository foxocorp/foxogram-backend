package app.foxochat.dto.api.response;

import app.foxochat.model.Channel;
import app.foxochat.model.Message;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "Channel")
public class ChannelDTO {

	private long id;

	private String displayName;

	private String name;

	private AttachmentDTO icon;

	private int type;

	private long flags;

	private int memberCount;

	private UserDTO owner;

	private long createdAt;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private MessageDTO lastMessage;

	public ChannelDTO(Channel channel, Message lastMessage) {
		this.id = channel.getId();
		this.displayName = channel.getDisplayName();
		this.name = channel.getName();
		if (channel.getIcon() != null) {
			this.icon = new AttachmentDTO(channel.getIcon());
		}
		this.type = channel.getType();
		this.flags = channel.getFlags();
		if (channel.getMembers() != null) {
			this.memberCount = channel.getMembers().size();
		}
		if (lastMessage != null) {
			this.lastMessage = new MessageDTO(lastMessage, false);
		}
		this.owner = new UserDTO(channel.getOwner(), null, null, false, false, false);
		this.createdAt = channel.getCreatedAt();
	}
}
