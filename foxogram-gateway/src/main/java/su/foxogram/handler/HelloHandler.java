package su.foxogram.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import su.foxogram.constant.GatewayConstant;
import su.foxogram.constant.UserConstant;
import su.foxogram.dto.gateway.EventDTO;
import su.foxogram.dto.gateway.response.HelloDTO;
import su.foxogram.exception.user.UserUnauthorizedException;
import su.foxogram.handler.structure.BaseHandler;
import su.foxogram.model.Session;
import su.foxogram.service.AuthenticationService;
import su.foxogram.service.RabbitService;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class HelloHandler implements BaseHandler {

	private final AuthenticationService authenticationService;

	private final ObjectMapper objectMapper;

	private final RabbitService rabbitService;

	public HelloHandler(AuthenticationService authenticationService, ObjectMapper objectMapper, RabbitService rabbitService) {
		this.authenticationService = authenticationService;
		this.objectMapper = objectMapper;
		this.rabbitService = rabbitService;
	}

	@Override
	public int getOpcode() {
		return GatewayConstant.Opcode.IDENTIFY.ordinal();
	}

	@Override
	public void handle(WebSocketSession session, ConcurrentHashMap<String, Session> sessions, EventDTO payload) throws UserUnauthorizedException, IOException {
		String accessToken = (String) payload.getD().get("token");

		Long userId = authenticationService.authenticate(accessToken);
		Session userSession = sessions.get(session.getId());
		userSession.setUserId(userId);
		userSession.setLastPingTimestamp(System.currentTimeMillis());

		session.sendMessage(new TextMessage(objectMapper.writeValueAsString(new HelloDTO())));
		rabbitService.send(userId, UserConstant.Status.ONLINE.getStatus());
		log.info("Authenticated session ({}) with user id {}", session.getId(), userId);
	}
}
