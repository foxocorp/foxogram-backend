package su.foxogram.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import su.foxogram.constants.GatewayConstants;
import su.foxogram.dtos.gateway.EventDTO;
import su.foxogram.dtos.gateway.response.HelloDTO;
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
public class HelloHandler implements BaseHandler {

	private final AuthenticationService authenticationService;

	private final ObjectMapper objectMapper;

	public HelloHandler(AuthenticationService authenticationService, ObjectMapper objectMapper) {
		this.authenticationService = authenticationService;
		this.objectMapper = objectMapper;
	}

	@Override
	public int getOpcode() {
		return GatewayConstants.Opcode.IDENTIFY.ordinal();
	}

	@Override
	public void handle(WebSocketSession session, ConcurrentHashMap<String, Session> sessions, EventDTO payload) throws UserEmailNotVerifiedException, UserUnauthorizedException, IOException {
		String accessToken = (String) payload.getD().get("token");

		User user = authenticationService.authenticate(accessToken);
		Session userSession = sessions.get(session.getId());
		userSession.setUserId(user.getId());
		userSession.setLastPingTimestamp(System.currentTimeMillis());

		session.sendMessage(new TextMessage(objectMapper.writeValueAsString(new HelloDTO())));
		log.info("Authenticated session ({}) with user id {}", session.getId(), user.getId());
	}
}
