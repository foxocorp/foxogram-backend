package su.foxogram.dtos.gateway.response;

import lombok.Getter;
import lombok.Setter;
import su.foxogram.dtos.gateway.OpcodeDTO;

import java.util.Map;

@Getter
@Setter
public class OkDTO extends OpcodeDTO {

	private Map<String, Integer> d;

	public OkDTO(int opcode) {
		super(opcode);
		this.d = Map.of("heartbeat_interval", 30000);
	}
}
