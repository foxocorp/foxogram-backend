package su.foxogram.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import su.foxogram.constants.GatewayEventsConstants;
import su.foxogram.dtos.gateway.GatewayEventDTO;
import su.foxogram.handlers.structures.BaseHandler;
import su.foxogram.models.Session;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class PingHandler implements BaseHandler {

	@Override
	public int getEvent() {
		return GatewayEventsConstants.Auth.PING.getValue();
	}

	@Override
	public void handle(WebSocketSession session, ConcurrentHashMap<String, Session> sessions, GatewayEventDTO payload) {
		Session userSession = sessions.get(session.getId());

		if (userSession.isAuthenticated()) {
			log.info("Get ping from session ({})", session.getId());
			userSession.setLastPingTimestamp(System.currentTimeMillis());
		}
	}
}
