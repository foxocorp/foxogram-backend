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
import su.foxogram.handler.structure.BaseHandler;
import su.foxogram.model.Session;
import su.foxogram.service.AuthenticationService;
import su.foxogram.service.UserService;
import su.foxogram.service.impl.UserServiceImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class HelloHandler implements BaseHandler {

	private final AuthenticationService authenticationService;

	private final ObjectMapper objectMapper;

	private final UserService userService;

	public HelloHandler(AuthenticationService authenticationService, ObjectMapper objectMapper, UserServiceImpl userService) {
		this.authenticationService = authenticationService;
		this.objectMapper = objectMapper;
		this.userService = userService;
	}

	@Override
	public int getOpcode() {
		return GatewayConstant.Opcode.IDENTIFY.ordinal();
	}

	@Override
	public void handle(WebSocketSession session, ConcurrentHashMap<String, Session> sessions, EventDTO payload) throws Exception {
		String accessToken = ((Map<String, String>) payload.getD()).get("token");

		long userId = authenticationService.getUser(accessToken, false).getId();
		Session userSession = sessions.get(session.getId());
		userSession.setUserId(userId);
		userSession.setLastPingTimestamp(System.currentTimeMillis());

		session.sendMessage(new TextMessage(objectMapper.writeValueAsString(new HelloDTO())));
		userService.setStatus(userId, UserConstant.Status.ONLINE.getStatus());
		log.info("Authenticated session ({}) with user id {}", session.getId(), userId);
	}
}
