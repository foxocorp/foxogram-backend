package su.foxogram.handlers.structures;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import su.foxogram.constants.CloseCodesConstants;
import su.foxogram.constants.ExceptionsConstants;
import su.foxogram.constants.GatewayConstants;
import su.foxogram.dtos.gateway.EventDTO;
import su.foxogram.exceptions.user.UserUnauthorizedException;
import su.foxogram.models.Session;

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

	@Autowired
	public EventHandler(EventHandlerRegistry handlerRegistry, ObjectMapper objectMapper) {
		this.handlerRegistry = handlerRegistry;
		this.objectMapper = objectMapper;

		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1, Thread.ofVirtual().factory());

		Runnable task = () -> {
			sessions.values().forEach(session -> {
				long lastPingTimestamp = session.getLastPingTimestamp();

				long timeout = (GatewayConstants.HEARTBEAT_INTERVAL + GatewayConstants.HEARTBEAT_TIMEOUT);

				if (lastPingTimestamp < (System.currentTimeMillis() - timeout)) {
					try {
						session.getWebSocketSession().close(CloseCodesConstants.HEARTBEAT_TIMEOUT);
						log.info("Session closed due to heartbeat timeout: {}", session.getWebSocketSession().getId());
					} catch (IOException e) {
						log.error("Error closing session: {}", session.getWebSocketSession().getId(), e);
						throw new RuntimeException(e);
					}
				}
			});
		};

		executor.scheduleAtFixedRate(task, 0, 30, TimeUnit.SECONDS);
	}

	@Override
	public void afterConnectionEstablished(@NonNull WebSocketSession session) {
		log.info("Connection for session ({}) established", session.getId());
		sessions.put(session.getId(), new Session(0, session));
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus status) {
		log.info("Connection for session ({}) closed with status {} ({})", session.getId(), status.getReason(), status.getCode());
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
			}
		} catch (UserUnauthorizedException e) {
			log.error(ExceptionsConstants.Messages.SERVER_EXCEPTION.getValue(), null, null, message);

			session.close(CloseCodesConstants.UNAUTHORIZED);
		} catch (Exception e) {
			log.error(ExceptionsConstants.Messages.SERVER_EXCEPTION.getValue(), null, null, message);
			log.error(ExceptionsConstants.Messages.SERVER_EXCEPTION_STACKTRACE.getValue(), e);
		}
	}
}
