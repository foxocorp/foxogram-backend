package app.foxochat.handler;

import app.foxochat.constant.CloseCodeConstant;
import app.foxochat.constant.GatewayConstant;
import app.foxochat.dto.gateway.EventDTO;
import app.foxochat.dto.gateway.response.HeartbeatACKDTO;
import app.foxochat.handler.structure.BaseHandler;
import app.foxochat.model.Session;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class HeartbeatHandler implements BaseHandler {

	private final ObjectMapper objectMapper;

	public HeartbeatHandler(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public int getOpcode() {
		return GatewayConstant.Opcode.HEARTBEAT.ordinal();
	}

	@Override
	public void handle(WebSocketSession session, ConcurrentHashMap<String, Session> sessions, EventDTO payload) throws IOException {
		Session userSession = sessions.get(session.getId());

		if (!userSession.isAuthenticated()) session.close(CloseCodeConstant.UNAUTHORIZED);

		userSession.setLastPingTimestamp(System.currentTimeMillis());

		session.sendMessage(new TextMessage(objectMapper.writeValueAsString(new HeartbeatACKDTO())));
		log.debug("Got heartbeat from session ({})", session.getId());
	}
}
