package su.foxochat.handler.structure;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import su.foxochat.constant.CloseCodeConstant;
import su.foxochat.constant.ExceptionConstant;
import su.foxochat.constant.GatewayConstant;
import su.foxochat.constant.UserConstant;
import su.foxochat.dto.gateway.EventDTO;
import su.foxochat.exception.user.UserUnauthorizedException;
import su.foxochat.model.Session;
import su.foxochat.service.UserService;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class EventHandler extends TextWebSocketHandler {

	private final EventHandlerRegistry handlerRegistry;

	private final ObjectMapper objectMapper;

	@Getter
	private final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

	private final UserService userService;

	public EventHandler(EventHandlerRegistry handlerRegistry, ObjectMapper objectMapper, UserService userService) {
		this.handlerRegistry = handlerRegistry;
		this.objectMapper = objectMapper;
		this.userService = userService;

		try (ScheduledExecutorService executor = Executors.newScheduledThreadPool(1, Thread.ofVirtual().factory())) {
			Runnable task = () -> sessions.values().forEach(session -> {
				long lastPingTimestamp = session.getLastPingTimestamp();

				long timeout = (GatewayConstant.HEARTBEAT_INTERVAL + GatewayConstant.HEARTBEAT_TIMEOUT);

				if (lastPingTimestamp < (System.currentTimeMillis() - timeout)) {
					try {
						session.getWebSocketSession().close(CloseCodeConstant.HEARTBEAT_TIMEOUT);
						log.debug("Session closed due to heartbeat timeout: {}", session.getWebSocketSession().getId());
					} catch (IOException e) {
						log.error("Error closing session: {}", session.getWebSocketSession().getId(), e);
						throw new RuntimeException(e);
					}
				}
			});

			executor.scheduleAtFixedRate(task, 0, 30, TimeUnit.SECONDS);
		} catch (Exception e) {
			log.error("Error initializing thread: {}", e.getMessage());
		}
	}

	@Override
	public void afterConnectionEstablished(@NonNull WebSocketSession session) {
		log.debug("Connection for session ({}) established", session.getId());
		sessions.put(session.getId(), new Session(session));
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus status) throws Exception {
		log.debug("Connection for session ({}) closed with status {} ({})", session.getId(), status.getReason(), status.getCode());

		Session userSession = sessions.get(session.getId());

		if (userSession.isAuthenticated()) {
			userService.setStatus(userSession.getUserId(), UserConstant.Status.ONLINE.getStatus());
		}
		sessions.remove(session.getId());
	}

	@Override
	protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
		try {
			EventDTO payload = objectMapper.readValue(message.getPayload(), EventDTO.class);
			int opcode = payload.getOp();

			BaseHandler handler = handlerRegistry.getHandler(opcode);

			if (handler != null) {
				handler.handle(session, sessions, payload);
				log.debug("Handling {} event with opcode {}", payload.getT(), payload.getOp());
			}
		} catch (UserUnauthorizedException e) {
			log.error(ExceptionConstant.Messages.SERVER_EXCEPTION.getValue(), null, null, message);

			session.close(CloseCodeConstant.UNAUTHORIZED);
		} catch (Exception e) {
			log.error(ExceptionConstant.Messages.SERVER_EXCEPTION.getValue(), null, null, message);
			log.error(ExceptionConstant.Messages.SERVER_EXCEPTION_STACKTRACE.getValue(), e);
		}
	}
}
