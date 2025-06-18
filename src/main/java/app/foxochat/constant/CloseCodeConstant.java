package app.foxochat.constant;

import org.springframework.web.socket.CloseStatus;

public class CloseCodeConstant {

	public static final CloseStatus UNAUTHORIZED = new CloseStatus(4001, "Unauthorized");

	public static final CloseStatus HEARTBEAT_TIMEOUT = new CloseStatus(4002, "Heartbeat timeout");
}
