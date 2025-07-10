package app.foxochat.dto.api.response;

import app.foxochat.constant.MemberConstant;
import app.foxochat.model.Channel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "Channel")
public class ChannelShortDTO {

    private long id;

    private String displayName;

    private String name;

    private AvatarDTO avatar;

    private AvatarDTO banner;

    private long ownerId;

    private int memberCount;

    private int type;

    private long flags;

    public ChannelShortDTO(Channel channel) {
        this.id = channel.getId();
        this.displayName = channel.getDisplayName();
        this.name = channel.getName();
        if (channel.getAvatar() != null) {
            this.avatar = new AvatarDTO(channel.getAvatar());
        }
        if (channel.getBanner() != null) {
            this.banner = new AvatarDTO(channel.getBanner());
        }
        this.type = channel.getType();
        this.flags = channel.getFlags();
        this.memberCount = channel.getMembers().size();
        this.ownerId = channel.getMembers().stream()
                .filter(m -> m.hasPermission(MemberConstant.Permissions.OWNER))
                .findFirst().get().getUser().getId();
    }
}
