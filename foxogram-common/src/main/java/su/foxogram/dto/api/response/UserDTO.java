package su.foxogram.dto.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.model.User;

import java.util.List;

@Getter
@Setter
@Schema(name = "User")
public class UserDTO {

	private long id;

	private AttachmentDTO avatar;

	private String displayName;

	private String username;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String email;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<Long> channels;

	private int status;

	private long statusUpdatedAt;

	private long flags;

	private int type;

	private long createdAt;

	@SuppressWarnings("unused")
	public UserDTO() {}

	public UserDTO(User user, List<Long> channels, boolean includeEmail, boolean includeChannels) {
		this.id = user.getId();
		if (user.getAvatar() != null) {
			this.avatar = new AttachmentDTO(user.getAvatar());
		}
		this.displayName = user.getDisplayName();
		this.username = user.getUsername();
		if (includeEmail) {
			this.email = user.getEmail();
		}
		if (includeChannels) {
			this.channels = channels;
		}
		this.status = user.getStatus();
		this.statusUpdatedAt = user.getStatusUpdatedAt();
		this.flags = user.getFlags();
		this.type = user.getType();
		this.createdAt = user.getCreatedAt();
	}
}
