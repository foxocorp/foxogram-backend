package su.foxogram.dto.gateway.response

import lombok.Getter
import lombok.Setter
import su.foxogram.constant.GatewayConstant

@Getter
@Setter
data class HeartbeatACKDTO(
    val op: Int = GatewayConstant.Opcode.HEARTBEAT_ACK.ordinal
)
