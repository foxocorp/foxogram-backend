package su.foxogram.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import su.foxogram.constants.GatewayEventsConstants;
import su.foxogram.dtos.gateway.GatewayEventDTO;
import su.foxogram.dtos.gateway.response.OkDTO;
import su.foxogram.exceptions.user.UserEmailNotVerifiedException;
import su.foxogram.exceptions.user.UserUnauthorizedException;
import su.foxogram.handlers.structures.BaseHandler;
import su.foxogram.models.Session;
import su.foxogram.models.User;
import su.foxogram.services.AuthenticationService;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class AuthenticationHandler implements BaseHandler {

	private final AuthenticationService authenticationService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public AuthenticationHandler(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@Override
	public int getEvent() {
		return GatewayEventsConstants.Auth.HELLO.getValue();
	}

	@Override
	public void handle(WebSocketSession session, ConcurrentHashMap<String, Session> sessions, GatewayEventDTO payload) throws IOException, UserEmailNotVerifiedException, UserUnauthorizedException {
		String accessToken = payload.getD().get("token");

		User user = authenticationService.authUser(accessToken, false, true);
		Session userSession = sessions.get(session.getId());
		userSession.setUserId(user.getId());
		userSession.setLastPingTimestamp(System.currentTimeMillis());

		session.sendMessage(new TextMessage(objectMapper.writeValueAsString(new OkDTO(GatewayEventsConstants.Auth.OK.getValue()))));
		log.info("Authenticated session ({}) with user id {}", session.getId(), user.getId());
	}
}
