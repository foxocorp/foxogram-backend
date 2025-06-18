package app.foxochat.dto.api.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExceptionDTO {

	private boolean ok;

	private int code;

	private String message;

	public ExceptionDTO(boolean ok, int code, String message) {
		this.ok = ok;
		this.code = code;
		this.message = message;
	}
}
