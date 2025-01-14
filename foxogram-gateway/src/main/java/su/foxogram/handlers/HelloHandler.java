package su.foxogram.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import su.foxogram.constants.GatewayConstants;
import su.foxogram.dtos.gateway.GatewayEventDTO;
import su.foxogram.dtos.gateway.response.HelloDTO;
import su.foxogram.exceptions.user.UserEmailNotVerifiedException;
import su.foxogram.exceptions.user.UserUnauthorizedException;
import su.foxogram.handlers.structures.BaseHandler;
import su.foxogram.models.Session;
import su.foxogram.models.User;
import su.foxogram.services.AuthenticationService;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import static su.foxogram.dtos.gateway.BaseDTO.objectMapper;

@Slf4j
@Component
public class HelloHandler implements BaseHandler {

	private final AuthenticationService authenticationService;

	public HelloHandler(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@Override
	public int getOpcode() {
		return GatewayConstants.Opcode.IDENTIFY.ordinal();
	}

	@Override
	public void handle(WebSocketSession session, ConcurrentHashMap<String, Session> sessions, GatewayEventDTO payload) throws UserEmailNotVerifiedException, UserUnauthorizedException, IOException {
		String accessToken = (String) payload.getD().get("token");

		User user = authenticationService.authUser(accessToken, true, true);
		Session userSession = sessions.get(session.getId());
		userSession.setUserId(user.getId());
		userSession.setLastPingTimestamp(System.currentTimeMillis());

		session.sendMessage(new TextMessage(objectMapper.writeValueAsString(new HelloDTO())));
		log.info("Authenticated session ({}) with user id {}", session.getId(), user.getId());
	}
}
