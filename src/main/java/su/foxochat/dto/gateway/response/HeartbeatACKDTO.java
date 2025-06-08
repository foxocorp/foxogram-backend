package su.foxochat.dto.gateway.response;

import lombok.Getter;
import lombok.Setter;
import su.foxochat.constant.GatewayConstant;

@Getter
@Setter
public class HeartbeatACKDTO {

	private int op;

	public HeartbeatACKDTO() {
		this.op = GatewayConstant.Opcode.HEARTBEAT_ACK.ordinal();
	}
}
