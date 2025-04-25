package su.foxogram.dtos.api.response;

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

	public AttachmentDTO(long id, String uuid, String filename, String contentType, long flags) {
		this.id = id;
		this.uuid = uuid;
		this.filename = filename;
		this.contentType = contentType;
		this.flags = flags;
	}
}
