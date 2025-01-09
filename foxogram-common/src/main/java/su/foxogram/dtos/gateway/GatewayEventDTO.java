package su.foxogram.dtos.gateway;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class GatewayEventDTO {

	private int op;

	private Map<String, String> d;

	private int s;

	public GatewayEventDTO() {
	}

	public GatewayEventDTO(int opcode, Map<String, String> data, int sequence) {
		this.op = opcode;
		this.d = data;
		this.s = sequence;
	}
}
