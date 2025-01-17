package su.foxogram.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

@Getter
@Setter
public class Session {

	private long userId;

	private long lastPingTimestamp;

	private int sequence;

	private WebSocketSession webSocketSession;

	public Session(long userId, WebSocketSession webSocketSession) {
		this.userId = userId;
		this.lastPingTimestamp = System.currentTimeMillis();
		this.sequence = 0;
		this.webSocketSession = webSocketSession;
	}

	public boolean isAuthenticated() {
		return userId != 0;
	}

	public void increaseSequence() {
		this.sequence++;
	}
}
