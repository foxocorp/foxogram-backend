package su.foxochat.dto.gateway.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExceptionDTO {

	private int op;

	private String m;

	public ExceptionDTO(int opcode, String message) {
		this.op = opcode;
		this.m = message;
	}
}
