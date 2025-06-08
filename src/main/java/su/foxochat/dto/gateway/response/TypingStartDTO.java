
package su.foxochat.dto.gateway.response;

import lombok.Getter;
import lombok.Setter;
import su.foxochat.constant.GatewayConstant;

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
