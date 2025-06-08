package su.foxochat.dto.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import su.foxochat.model.User;

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

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<Long> contacts;

	private long flags;

	private int type;

	private long createdAt;

	@SuppressWarnings("unused")
	public UserDTO() {}

	public UserDTO(User user, List<Long> channels, List<Long> contacts, boolean includeEmail, boolean includeChannels, boolean includeContacts) {
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
		if (includeContacts) {
			this.contacts = contacts;
		}
		this.status = user.getStatus();
		this.statusUpdatedAt = user.getStatusUpdatedAt();
		this.flags = user.getFlags();
		this.type = user.getType();
		this.createdAt = user.getCreatedAt();
	}
}
