package su.foxogram.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import su.foxogram.dtos.gateway.EventDTO;
import su.foxogram.handlers.structures.EventHandler;
import su.foxogram.models.Session;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketService {

	private final EventHandler webSocketHandler;

	private final ObjectMapper objectMapper;

	@Autowired
	public WebSocketService(EventHandler webSocketHandler, ObjectMapper objectMapper) {
		this.webSocketHandler = webSocketHandler;
		this.objectMapper = objectMapper;
	}

	public void sendMessageToAll(int opcode, Map<String, Object> data, String type) throws Exception {
		ConcurrentHashMap<String, Session> sessions = webSocketHandler.getSessions();
		for (Session session : sessions.values()) {
			WebSocketSession wsSession = session.getWebSocketSession();

			int seqNumber = session.getSequence();
			session.increaseSequence();

			if (!wsSession.isOpen()) return;

			wsSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(new EventDTO(opcode, data, seqNumber, type))));
		}
	}

	public void sendMessageToSessions(List<Long> userIds, int opcode, Map<String, Object> data, String type) throws Exception {
		ConcurrentHashMap<String, Session> sessions = webSocketHandler.getSessions();
		for (Session session : sessions.values()) {
			if (session != null) {
				if (!userIds.contains(session.getUserId())) return;

				int seqNumber = session.getSequence();
				session.increaseSequence();
				WebSocketSession wsSession = session.getWebSocketSession();

				if (!wsSession.isOpen()) return;

				wsSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(new EventDTO(opcode, data, seqNumber, type))));
			}
		}
	}
}
