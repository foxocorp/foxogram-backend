package su.foxogram.handlers.structures;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import su.foxogram.dtos.gateway.EventDTO;
import su.foxogram.models.Session;

import java.util.concurrent.ConcurrentHashMap;

@Component
public interface BaseHandler {

	int getOpcode();

	void handle(WebSocketSession session, ConcurrentHashMap<String, Session> sessions, EventDTO payload) throws Exception;
}
