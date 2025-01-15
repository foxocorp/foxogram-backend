package su.foxogram.dtos.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.models.Member;

@Getter
@Setter
@Schema(name = "Member")
public class MemberDTO {

	private long id;

	private UserDTO user;

	private ChannelDTO channel;

	private long permissions;

	private long joinedAt;

	public MemberDTO(Member member) {
		this.id = member.getId();
		this.user = new UserDTO(member.getUser(), null, false, false);
		this.channel = new ChannelDTO(member.getChannel(), false);
		this.permissions = member.getPermissions();
		this.joinedAt = member.getJoinedAt();
	}
}

