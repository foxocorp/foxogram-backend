package app.foxochat.handler.structure;

import app.foxochat.dto.gateway.EventDTO;
import app.foxochat.model.Session;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Component
public interface BaseHandler {

    int getOpcode();

    void handle(WebSocketSession session, ConcurrentHashMap<String, Session> sessions, EventDTO payload)
            throws Exception;
}
