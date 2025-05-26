package su.foxogram.dto.gateway;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventDTO {

	private int op;

	private Object d;

	private int s;

	private String t;

	public EventDTO() {
	}

	public EventDTO(int opcode, Object data, int sequence, String type) {
		this.op = opcode;
		this.d = data;
		this.s = sequence;
		this.t = type;
	}
}
