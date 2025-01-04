package su.foxogram.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import su.foxogram.handlers.structures.EventHandler;
import su.foxogram.models.Session;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketService {

	private final EventHandler webSocketHandler;

	@Autowired
	public WebSocketService(EventHandler webSocketHandler) {
		this.webSocketHandler = webSocketHandler;
	}

	public void sendMessageToAll(String message) throws Exception {
		ConcurrentHashMap<String, Session> sessions = webSocketHandler.getSessions();
		for (Session session : sessions.values()) {
			WebSocketSession wsSession = session.getWebSocketSession();
			if (wsSession.isOpen()) {
				wsSession.sendMessage(new TextMessage(message));
			}
		}
	}

	public void sendMessageToSessions(List<Long> userIds, String message) throws Exception {
		ConcurrentHashMap<String, Session> sessions = webSocketHandler.getSessions();
		for (Session session : sessions.values()) {
			if (session != null) {
				if (!userIds.contains(session.getUserId())) return;

				WebSocketSession wsSession = session.getWebSocketSession();

				if (wsSession.isOpen()) return;

				wsSession.sendMessage(new TextMessage(message));
			}
		}
	}
}