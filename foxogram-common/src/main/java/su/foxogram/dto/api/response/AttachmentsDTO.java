package su.foxogram.dto.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "Attachments")
public class AttachmentsDTO {

	public String url;

	public long id;

	public AttachmentsDTO(String url, long id) {
		this.url = url;
		this.id = id;
	}
}
