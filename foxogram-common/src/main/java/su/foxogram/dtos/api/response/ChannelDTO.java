package su.foxogram.dtos.api.response;

import lombok.Getter;
import lombok.Setter;
import su.foxogram.models.Channel;

@Getter
@Setter
public class ChannelDTO {

	private String displayName;

	private String name;

	private String icon;

	private int type;

	private String owner;

	private long createdAt;

	public ChannelDTO(Channel channel) {
		this.displayName = channel.getDisplayName();
		this.name = channel.getName();
		this.icon = channel.getIcon();
		this.type = channel.getType();
		this.owner = channel.getOwner();
		this.createdAt = channel.getCreatedAt();
	}
}
