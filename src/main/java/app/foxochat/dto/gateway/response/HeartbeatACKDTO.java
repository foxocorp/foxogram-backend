package app.foxochat.dto.gateway.response;

import app.foxochat.constant.GatewayConstant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HeartbeatACKDTO {

	private int op;

	public HeartbeatACKDTO() {
		this.op = GatewayConstant.Opcode.HEARTBEAT_ACK.ordinal();
	}
}
