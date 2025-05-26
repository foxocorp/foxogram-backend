package su.foxogram.dto.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.model.Attachment;

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
