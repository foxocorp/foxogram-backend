package app.foxochat.dto.gateway.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO {

	private long id;

	private String username;

	private String displayName;

	private String bio;

	private int status;

	private long avatar;

	private long banner;

	public UserUpdateDTO(long id, String username, String displayName, String bio, int status, long avatar, long banner) {
		this.id = id;
		this.username = username;
		this.displayName = displayName;
		this.bio = bio;
		this.status = status;
		this.avatar = avatar;
		this.banner = banner;
	}
}
