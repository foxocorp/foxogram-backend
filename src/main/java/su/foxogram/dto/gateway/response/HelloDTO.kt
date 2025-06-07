package su.foxogram.dto.gateway.response

import lombok.Getter
import lombok.Setter
import su.foxogram.constant.GatewayConstant
import java.util.Map

@Getter
@Setter
data class HelloDTO(
    val op: Int = GatewayConstant.Opcode.HELLO.ordinal,

    val d: MutableMap<String, Int>? = Map.of<String, Int>("heartbeat_interval", GatewayConstant.HEARTBEAT_INTERVAL)
)
