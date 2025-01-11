package su.foxogram.dtos.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.models.User;

import java.util.List;

@Getter
@Setter
public class UserDTO {

	private long id;

	private String avatar;

	private String displayName;

	private String username;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String email;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<Long> channels;

	private long flags;

	private long type;

	private long createdAt;

	public UserDTO(User user, List<Long> channels, boolean includeEmail, boolean includeChannels) {
		this.id = user.getId();
		this.avatar = user.getAvatar();
		this.displayName = user.getDisplayName();
		this.username = user.getUsername();
		if (includeEmail) {
			this.email = user.getEmail();
		}
		if (includeChannels) {
			this.channels = channels;
		}
		this.flags = user.getFlags();
		this.type = user.getType();
		this.createdAt = user.getCreatedAt();
	}
}
