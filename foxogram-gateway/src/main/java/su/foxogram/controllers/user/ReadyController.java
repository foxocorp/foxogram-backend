package su.foxogram.controllers.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.*;
import su.foxogram.dtos.ReadyPayload;
import su.foxogram.exceptions.user.UserUnauthorizedException;
import su.foxogram.models.User;
import su.foxogram.services.AuthenticationService;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
public class ReadyController implements WebSocketHandler {

	final AuthenticationService authenticationService;

	final Logger logger = LoggerFactory.getLogger(ReadyController.class);

	final ObjectMapper mapper = new ObjectMapper();

	@Autowired
	public ReadyController(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		logger.info("Session CREATED with ID {} and with URI {}", session.getId(), session.getUri());
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		Map<String, List<String>> headers = session.getHandshakeHeaders();
		User user = authenticationService.getUser(Objects.requireNonNull(headers.get(HttpHeaders.AUTHORIZATION)).toString());

		if (user == null) throw new UserUnauthorizedException();

		ReadyPayload payload = mapper.readValue((String) message.getPayload(), ReadyPayload.class);
		logger.info("got READY event with CODE {} and NAME {}", payload.getCode(), payload.getName());
		session.sendMessage(new TextMessage("ok :)"));
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		logger.error("There's an ERROR with transporting, SESSION ID {} and URI {}", session.getId(), session.getUri());
		throw new Exception(exception);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
		logger.info("Session DESTROYED with STATUS {} ({}) and with ID {} and with URI {}", closeStatus.getReason(), closeStatus.getCode(), session.getId(), session.getUri());
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}
}
