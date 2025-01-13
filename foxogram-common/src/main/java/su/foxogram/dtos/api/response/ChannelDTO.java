package su.foxogram.dtos.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.models.Channel;

@Getter
@Setter
@Schema(name = "Channel")
public class ChannelDTO {

	private long id;

	private String displayName;

	private String name;

	private String icon;

	private int type;

	private String owner;

	private long createdAt;

	private MessageDTO lastMessage;

	public ChannelDTO(Channel channel) {
		this.id = channel.getId();
		this.displayName = channel.getDisplayName();
		this.name = channel.getName();
		this.icon = channel.getIcon();
		this.type = channel.getType();
		if (channel.getLastMessage() != null) this.lastMessage = new MessageDTO(channel.getLastMessage());
		else this.lastMessage = null;
		this.owner = channel.getOwner();
		this.createdAt = channel.getCreatedAt();
	}
}
