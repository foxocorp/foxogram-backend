package su.foxogram.dtos.gateway;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpcodeDTO {
	private int op;

	public OpcodeDTO(int opcode) {
		this.op = opcode;
	}
}
