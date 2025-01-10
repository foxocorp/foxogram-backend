package su.foxogram.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import su.foxogram.constants.GatewayConstants;
import su.foxogram.dtos.gateway.GatewayEventDTO;
import su.foxogram.dtos.gateway.response.HeartbeatACKDTO;
import su.foxogram.handlers.structures.BaseHandler;
import su.foxogram.models.Session;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import static su.foxogram.dtos.gateway.BaseDTO.objectMapper;

@Slf4j
@Component
public class HeartbeatHandler implements BaseHandler {

	@Override
	public int getOpcode() {
		return GatewayConstants.Opcode.HEARTBEAT.ordinal();
	}

	@Override
	public void handle(WebSocketSession session, ConcurrentHashMap<String, Session> sessions, GatewayEventDTO payload) throws IOException {
		Session userSession = sessions.get(session.getId());

		if (userSession.isAuthenticated()) {
			userSession.setLastPingTimestamp(System.currentTimeMillis());

			session.sendMessage(new TextMessage(objectMapper.writeValueAsString(new HeartbeatACKDTO())));
			log.info("Got heartbeat from session ({})", session.getId());
		}
	}
}
