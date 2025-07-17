package app.foxochat.dto.api.response;

import app.foxochat.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(name = "User")
public class UserDTO {

    private long id;

    private AvatarDTO avatar;

    private AvatarDTO banner;

    private String displayName;

    private String username;

    private String bio;

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
    public UserDTO() {
    }

    public UserDTO(User user, List<Long> channels, List<Long> contacts, boolean withEmail, boolean withChannels,
                   boolean withContacts, boolean withAvatar, boolean withBanner) {
        this.id = user.getId();
        if (user.getAvatar() != null && withAvatar) {
            this.avatar = new AvatarDTO(user.getAvatar());
        }
        if (user.getBanner() != null && withBanner) {
            this.banner = new AvatarDTO(user.getBanner());
        }
        this.displayName = user.getDisplayName();
        this.username = user.getUsername();
        this.bio = user.getBio();
        if (withEmail) {
            this.email = user.getEmail();
        }
        if (channels != null && withChannels) {
            this.channels = channels;
        }
        if (contacts != null && withContacts) {
            this.contacts = contacts;
        }
        this.status = user.getStatus();
        this.statusUpdatedAt = user.getStatusUpdatedAt();
        this.flags = user.getFlags();
        this.type = user.getType();
        this.createdAt = user.getCreatedAt();
    }
}
