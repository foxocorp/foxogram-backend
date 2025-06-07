package su.foxogram.dto.gateway.response

import lombok.Getter
import lombok.Setter
import su.foxogram.constant.GatewayConstant
import java.util.Map

@Getter
@Setter
class TypingStartDTO(channelId: Long, userId: Long, timestamp: Long) {
    private val op: Int = GatewayConstant.Opcode.HELLO.ordinal

    private val d: MutableMap<String, Long>? =
        Map.of<String, Long>("channel_id", channelId, "user_id", userId, "timestamp", timestamp)

}
