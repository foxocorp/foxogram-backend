package app.foxochat.model;

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

    public Session(WebSocketSession webSocketSession) {
        this.lastPingTimestamp = System.currentTimeMillis();
        this.webSocketSession = webSocketSession;
    }

    public boolean isAuthenticated() {
        return this.userId > 0;
    }

    public void increaseSequence() {
        this.sequence++;
    }
}
