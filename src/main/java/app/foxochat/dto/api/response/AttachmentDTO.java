package app.foxochat.dto.api.response;

import app.foxochat.model.Attachment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "Attachment")
public class AttachmentDTO {

    public long id;

    public String uuid;

    public String filename;

    public String contentType;

    public long flags;

    public AttachmentDTO(Attachment attachment) {
        this.id = attachment.getId();
        this.uuid = attachment.getUuid();
        this.filename = attachment.getFilename();
        this.contentType = attachment.getContentType();
        this.flags = attachment.getFlags();
    }
}
