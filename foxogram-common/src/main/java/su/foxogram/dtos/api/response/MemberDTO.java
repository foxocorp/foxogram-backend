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

	private String username;

	private long channel;

	private long permissions;

	private long joinedAt;

	public MemberDTO(Member member) {
		this.id = member.getId();
		this.username = member.getUser().getUsername();
		this.channel = member.getChannel().getId();
		this.permissions = member.getPermissions();
		this.joinedAt = member.getJoinedAt();
	}
}

