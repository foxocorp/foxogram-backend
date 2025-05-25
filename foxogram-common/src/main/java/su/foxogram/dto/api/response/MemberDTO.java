package su.foxogram.dto.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.model.Member;

@Getter
@Setter
@Schema(name = "Member")
public class MemberDTO {

	private long id;

	private UserDTO user;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private ChannelDTO channel;

	private long permissions;

	private long joinedAt;

	public MemberDTO(Member member, boolean includeChannel) {
		this.id = member.getId();
		this.user = new UserDTO(member.getUser(), null, null, false, false, false);
		if (includeChannel) this.channel = new ChannelDTO(member.getChannel(), null);
		this.permissions = member.getPermissions();
		this.joinedAt = member.getJoinedAt();
	}
}

