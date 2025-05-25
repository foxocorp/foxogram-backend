package su.foxogram.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import su.foxogram.dto.gateway.EventDTO;
import su.foxogram.handler.structure.EventHandler;
import su.foxogram.model.Session;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
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
			log.debug("Sent message to all sessions (opcode: {}, type: {})", opcode, type);
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
				log.debug("Sent message to userIds ({}) with (opcode: {}, type: {})", userIds, opcode, type);
			}
		}
	}
}
