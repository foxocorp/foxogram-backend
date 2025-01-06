package su.foxogram.dtos.gateway.response;

import lombok.Getter;
import lombok.Setter;
import su.foxogram.dtos.gateway.OpcodeDTO;

@Getter
@Setter
public class OkDTO extends OpcodeDTO {

	private boolean ok;

	public OkDTO(int opcode, boolean ok) {
		super(opcode);
		this.ok = ok;
	}
}
