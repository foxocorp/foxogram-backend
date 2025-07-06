package app.foxochat.dto.gateway.response;

import app.foxochat.constant.GatewayConstant;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class TypingStartDTO {

    private int op;

    private Map<String, Long> d;

    public TypingStartDTO(long channelId, long userId, long timestamp) {
        this.op = GatewayConstant.Opcode.HELLO.ordinal();
        this.d = Map.of("channel_id", channelId, "user_id", userId, "timestamp", timestamp);
    }
}
