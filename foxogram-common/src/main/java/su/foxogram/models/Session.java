package su.foxogram.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

@Getter
@Setter
public class Session {

	private String id;

	private long userId;

	private WebSocketSession webSocketSession;

	public Session(String id, long userId, WebSocketSession webSocketSession) {
		this.id = id;
		this.userId = userId;
		this.webSocketSession = webSocketSession;
	}

	public boolean isAuthenticated() {
		return userId != 0;
	}
}
