package su.foxogram.dto.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "Ok")
public class OkDTO {

	private boolean ok;

	public OkDTO(boolean ok) {
		this.ok = ok;
	}
}
