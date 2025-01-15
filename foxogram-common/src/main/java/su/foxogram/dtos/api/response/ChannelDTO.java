package su.foxogram.dtos.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private MessageDTO lastMessage;

	public ChannelDTO(Channel channel, boolean includeLastMessage) {
		this.id = channel.getId();
		this.displayName = channel.getDisplayName();
		this.name = channel.getName();
		this.icon = channel.getIcon();
		this.type = channel.getType();
		if (includeLastMessage) {
			if (channel.getMessages() != null && !channel.getMessages().isEmpty())
				this.lastMessage = new MessageDTO(channel.getMessages().getLast());
			else this.lastMessage = null;
		}
		this.owner = channel.getOwner(); // TODO: change to owner id
		this.createdAt = channel.getCreatedAt();
	}
}
