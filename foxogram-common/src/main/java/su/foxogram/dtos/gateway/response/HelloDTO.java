package su.foxogram.dtos.gateway.response;

import lombok.Getter;
import lombok.Setter;
import su.foxogram.constants.GatewayConstants;

import java.util.Map;

@Getter
@Setter
public class HelloDTO {

	private int op;

	private Map<String, Integer> d;

	public HelloDTO() {
		this.op = GatewayConstants.Opcode.HELLO.ordinal();
		this.d = Map.of("heartbeat_interval", 30000);
	}
}
