package app.foxochat.handler;

import app.foxochat.constant.CloseCodeConstant;
import app.foxochat.constant.GatewayConstant;
import app.foxochat.dto.gateway.EventDTO;
import app.foxochat.dto.gateway.response.TypingStartDTO;
import app.foxochat.handler.structure.BaseHandler;
import app.foxochat.model.Member;
import app.foxochat.model.Session;
import app.foxochat.service.ChannelService;
import app.foxochat.service.GatewayService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class TypingStartHandler implements BaseHandler {

    private final GatewayService gatewayService;

    private final ObjectMapper objectMapper;

    private final ChannelService channelService;

    public TypingStartHandler(@Lazy GatewayService gatewayService, ObjectMapper objectMapper,
                              ChannelService channelService) {
        this.gatewayService = gatewayService;
        this.objectMapper = objectMapper;
        this.channelService = channelService;
    }

    @Override
    public int getOpcode() {
        return GatewayConstant.Opcode.DISPATCH.ordinal();
    }

    @Override
    public void handle(
            WebSocketSession session,
            ConcurrentHashMap<String, Session> sessions,
            EventDTO payload
    ) throws Exception {
        TypingStartDTO data = objectMapper.convertValue(payload.getD(), TypingStartDTO.class);
        long channelId = data.getD().get("channelId");
        Session userSession = sessions.get(session.getId());

        if (!userSession.isAuthenticated()) session.close(CloseCodeConstant.UNAUTHORIZED);

        List<Long> recipients =
                channelService.getById(channelId).get().getMembers().stream().map(Member::getId).toList();

        gatewayService.sendToSpecificSessions(recipients,
                GatewayConstant.Opcode.DISPATCH.ordinal(),
                new TypingStartDTO(channelId, userSession.getUserId(), System.currentTimeMillis()),
                GatewayConstant.Event.TYPING_START.getValue());
    }
}
