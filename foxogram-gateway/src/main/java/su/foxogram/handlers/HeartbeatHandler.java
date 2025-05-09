package su.foxogram.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import su.foxogram.constants.GatewayConstants;
import su.foxogram.dtos.gateway.EventDTO;
import su.foxogram.dtos.gateway.response.HeartbeatACKDTO;
import su.foxogram.handlers.structures.BaseHandler;
import su.foxogram.models.Session;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class HeartbeatHandler implements BaseHandler {

	private final ObjectMapper objectMapper;

	@Autowired
	public HeartbeatHandler(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public int getOpcode() {
		return GatewayConstants.Opcode.HEARTBEAT.ordinal();
	}

	@Override
	public void handle(WebSocketSession session, ConcurrentHashMap<String, Session> sessions, EventDTO payload) throws IOException {
		Session userSession = sessions.get(session.getId());

		if (userSession.isAuthenticated()) {
			userSession.setLastPingTimestamp(System.currentTimeMillis());

			session.sendMessage(new TextMessage(objectMapper.writeValueAsString(new HeartbeatACKDTO())));
			log.debug("Got heartbeat from session ({})", session.getId());
		}
	}
}
