package app.foxochat.service.impl;

import app.foxochat.dto.gateway.EventDTO;
import app.foxochat.handler.structure.EventHandler;
import app.foxochat.model.Session;
import app.foxochat.service.GatewayService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class GatewayServiceImpl implements GatewayService {

    private final EventHandler webSocketHandler;

    private final ObjectMapper objectMapper;

    public GatewayServiceImpl(EventHandler webSocketHandler, ObjectMapper objectMapper) {
        this.webSocketHandler = webSocketHandler;
        this.objectMapper = objectMapper;
    }

    @Async
    @Override
    public void sendToSpecificSessions(List<Long> userIds, int opcode, Object data, String type) {
        Map<String, Session> sessions = webSocketHandler.getSessions();
        log.debug("Trying to send message to users ({}) with (opcode: {}, type: {})", userIds, opcode, type);

        sessions.values().forEach(session -> {
            if (session != null && userIds.contains(session.getUserId())) {
                CompletableFuture.runAsync(() -> {
                    try {
                        int seqNumber = session.getSequence();
                        session.increaseSequence();

                        WebSocketSession wsSession = session.getWebSocketSession();
                        if (wsSession != null && wsSession.isOpen()) {
                            EventDTO event = new EventDTO(opcode, data, seqNumber, type);
                            String message = objectMapper.writeValueAsString(event);
                            wsSession.sendMessage(new TextMessage(message));

                            log.debug("Sent message to userId ({}) with (opcode: {}, type: {})",
                                    session.getUserId(), opcode, type);
                        }
                    } catch (Exception e) {
                        log.error("Failed to send message to userId ({})", session.getUserId(), e);
                    }
                });
            }
        });
    }
}
