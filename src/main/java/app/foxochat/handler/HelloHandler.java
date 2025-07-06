package app.foxochat.handler;

import app.foxochat.constant.GatewayConstant;
import app.foxochat.constant.UserConstant;
import app.foxochat.dto.gateway.EventDTO;
import app.foxochat.dto.gateway.response.HelloDTO;
import app.foxochat.handler.structure.BaseHandler;
import app.foxochat.model.Session;
import app.foxochat.service.AuthenticationService;
import app.foxochat.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class HelloHandler implements BaseHandler {

    private final AuthenticationService authenticationService;

    private final ObjectMapper objectMapper;

    private final UserService userService;

    public HelloHandler(AuthenticationService authenticationService, ObjectMapper objectMapper,
                        UserService userService) {
        this.authenticationService = authenticationService;
        this.objectMapper = objectMapper;
        this.userService = userService;
    }

    @Override
    public int getOpcode() {
        return GatewayConstant.Opcode.IDENTIFY.ordinal();
    }

    @Override
    public void handle(
            WebSocketSession session,
            ConcurrentHashMap<String, Session> sessions,
            EventDTO payload
    ) throws Exception {
        @SuppressWarnings("unchecked")
        String accessToken = ((Map<String, String>) payload.getD()).get("token");

        long userId = authenticationService.getUser(accessToken, true, false).getId();
        Session userSession = sessions.get(session.getId());
        userSession.setUserId(userId);
        userSession.setLastPingTimestamp(System.currentTimeMillis());

        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(new HelloDTO())));
        userService.setStatus(userId, UserConstant.Status.ONLINE.getStatus());
        log.debug("Authenticated session ({}) with user id {}", session.getId(), userId);
    }
}
