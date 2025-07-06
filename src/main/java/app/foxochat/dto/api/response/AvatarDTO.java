package app.foxochat.dto.api.response;

import app.foxochat.model.Avatar;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "Attachment")
public class AvatarDTO {

    public long id;

    public String uuid;

    public String filename;

    public String tumbhash;

    public AvatarDTO(Avatar avatar) {
        this.id = avatar.getId();
        this.uuid = avatar.getUuid();
        this.filename = avatar.getFilename();
        this.tumbhash = avatar.getTumbhash();
    }
}
