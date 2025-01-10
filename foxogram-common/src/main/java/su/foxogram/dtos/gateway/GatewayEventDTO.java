package su.foxogram.dtos.gateway;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class GatewayEventDTO {

	private int op;

	private Map<String, Object> d;

	private int s;

	private String t;

	public GatewayEventDTO() {
	}

	public GatewayEventDTO(int opcode, Map<String, Object> data, int sequence, String type) {
		this.op = opcode;
		this.d = data;
		this.s = sequence;
		this.t = type;
	}
}
