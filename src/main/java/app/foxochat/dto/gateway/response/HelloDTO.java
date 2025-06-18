package app.foxochat.dto.gateway.response;

import app.foxochat.constant.GatewayConstant;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class HelloDTO {

	private int op;

	private Map<String, Integer> d;

	public HelloDTO() {
		this.op = GatewayConstant.Opcode.HELLO.ordinal();
		this.d = Map.of("heartbeat_interval", GatewayConstant.HEARTBEAT_INTERVAL);
	}
}
