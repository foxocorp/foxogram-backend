package su.foxogram.dtos.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.constants.ChannelsConstants;
import su.foxogram.models.Channel;
import su.foxogram.models.Message;

@Getter
@Setter
@Schema(name = "Channel")
public class ChannelDTO {

	private long id;

	private String displayName;

	private String name;

	private String icon;

	private int type;

	private boolean isPublic;

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
		this.icon = channel.getIcon();
		this.type = channel.getType();
		this.flags = channel.getFlags();
		this.isPublic = channel.hasFlag(ChannelsConstants.Flags.PUBLIC);
		if (channel.getMembers() != null) {
			this.memberCount = channel.getMembers().size();
		}
		if (lastMessage != null) {
			this.lastMessage = new MessageDTO(lastMessage, null);
		}
		this.owner = new UserDTO(channel.getOwner(), null, false, false);
		this.createdAt = channel.getCreatedAt();
	}
}
