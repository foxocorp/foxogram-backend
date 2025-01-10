package su.foxogram.dtos.gateway.response;

import lombok.Getter;
import lombok.Setter;
import su.foxogram.constants.GatewayConstants;

@Getter
@Setter
public class HeartbeatACKDTO {

	private int op;

	public HeartbeatACKDTO() {
		this.op = GatewayConstants.Opcode.HEARTBEAT_ACK.ordinal();
	}
}
