package su.foxogram.handlers.structures;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import su.foxogram.constants.CloseCodesConstants;
import su.foxogram.constants.ExceptionsConstants;
import su.foxogram.dtos.gateway.GatewayEventDTO;
import su.foxogram.exceptions.user.UserUnauthorizedException;
import su.foxogram.models.Session;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class EventHandler extends TextWebSocketHandler {

	private final EventHandlerRegistry handlerRegistry;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Getter
	private final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

	public EventHandler(EventHandlerRegistry handlerRegistry) {
		this.handlerRegistry = handlerRegistry;
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
			GatewayEventDTO payload = objectMapper.readValue(message.getPayload(), GatewayEventDTO.class);
			int event = payload.getOp();

			BaseHandler handler = handlerRegistry.getHandler(event);

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