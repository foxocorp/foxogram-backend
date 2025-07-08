package app.foxochat.dto.api.response;

import app.foxochat.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "User")
public class UserShortDTO {

    private long id;

    private AvatarDTO avatar;

    private String displayName;

    private String username;

    private int status;

    private long statusUpdatedAt;

    @SuppressWarnings("unused")
    public UserShortDTO() {
    }

    public UserShortDTO(User user) {
        this.id = user.getId();
        if (user.getAvatar() != null) {
            this.avatar = new AvatarDTO(user.getAvatar());
        }
        this.displayName = user.getDisplayName();
        this.username = user.getUsername();
        this.status = user.getStatus();
        this.statusUpdatedAt = user.getStatusUpdatedAt();
    }
}
