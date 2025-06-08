package su.foxochat.handler.structure;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import su.foxochat.dto.gateway.EventDTO;
import su.foxochat.model.Session;

import java.util.concurrent.ConcurrentHashMap;

@Component
public interface BaseHandler {

	int getOpcode();

	void handle(WebSocketSession session, ConcurrentHashMap<String, Session> sessions, EventDTO payload) throws Exception;
}
