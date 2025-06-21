package app.foxochat.service.impl;

import app.foxochat.dto.gateway.EventDTO;
import app.foxochat.handler.structure.EventHandler;
import app.foxochat.model.Session;
import app.foxochat.service.GatewayService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class GatewayServiceImpl implements GatewayService {

	private final EventHandler webSocketHandler;

	private final ObjectMapper objectMapper;

	public GatewayServiceImpl(EventHandler webSocketHandler, ObjectMapper objectMapper) {
		this.webSocketHandler = webSocketHandler;
		this.objectMapper = objectMapper;
	}

	@Override
	public void sendMessageToSpecificSessions(List<Long> userIds, int opcode, Object data, String type) throws Exception {
		ConcurrentHashMap<String, Session> sessions = webSocketHandler.getSessions();
		log.debug("Trying to send message to users ({}) with (opcode: {}, type: {})", userIds, opcode, type);
		for (Session session : sessions.values()) {
			if (session != null) {
				if (!userIds.contains(session.getUserId())) continue;

				int seqNumber = session.getSequence();
				session.increaseSequence();
				WebSocketSession wsSession = session.getWebSocketSession();

				if (!wsSession.isOpen()) continue;

				wsSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(new EventDTO(opcode, data, seqNumber, type))));
				log.debug("Sent message to userId ({}) with (opcode: {}, type: {})", session.getUserId(), opcode, type);
			}
		}
	}
}
