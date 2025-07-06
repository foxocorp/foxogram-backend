package app.foxochat.dto.gateway.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelUpdateDTO {

    private long id;

    private String displayName;

    private String name;

    private long flags;

    private long avatar;

    public ChannelUpdateDTO(long id, String displayName, String name, long flags, long avatar) {
        this.id = id;
        this.displayName = displayName;
        this.name = name;
        this.flags = flags;
        this.avatar = avatar;
    }
}
