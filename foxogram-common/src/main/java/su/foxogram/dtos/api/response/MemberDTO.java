package su.foxogram.dtos.api.response;

import lombok.Getter;
import lombok.Setter;
import su.foxogram.models.Member;

@Getter
@Setter
public class MemberDTO {

	private long id;

	private String username;

	private String channel;

	private long permissions;

	private long joinedAt;

	public MemberDTO(Member member) {
		this.id = member.getId();
		this.username = member.getUser().getUsername();
		this.channel = member.getChannel().getName();
		this.permissions = member.getPermissions();
		this.joinedAt = member.getJoinedAt();
	}
}

