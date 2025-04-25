package su.foxogram.dtos.internal;

import lombok.Getter;
import lombok.Setter;
import su.foxogram.models.Attachment;

@Getter
@Setter
public class AttachmentPresignedDTO {

	private String url;

	private String uuid;

	private Attachment attachment;

	public AttachmentPresignedDTO(String url, String uuid, Attachment attachment) {
		this.url = url;
		this.uuid = uuid;
		this.attachment = attachment;
	}
}
