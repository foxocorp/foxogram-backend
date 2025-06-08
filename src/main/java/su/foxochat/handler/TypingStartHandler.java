package su.foxochat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import su.foxochat.constant.GatewayConstant;
import su.foxochat.dto.gateway.EventDTO;
import su.foxochat.dto.gateway.response.TypingStartDTO;
import su.foxochat.handler.structure.BaseHandler;
import su.foxochat.model.Member;
import su.foxochat.model.Session;
import su.foxochat.service.ChannelService;
import su.foxochat.service.GatewayService;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class TypingStartHandler implements BaseHandler {

	private final GatewayService gatewayService;

	private final ObjectMapper objectMapper;

	private final ChannelService channelService;

	public TypingStartHandler(@Lazy GatewayService gatewayService, ObjectMapper objectMapper, ChannelService channelService) {
		this.gatewayService = gatewayService;
		this.objectMapper = objectMapper;
		this.channelService = channelService;
	}

	@Override
	public int getOpcode() {
		return GatewayConstant.Opcode.DISPATCH.ordinal();
	}

	@Override
	public void handle(WebSocketSession session, ConcurrentHashMap<String, Session> sessions, EventDTO payload) throws Exception {
		TypingStartDTO data = objectMapper.convertValue(payload.getD(), TypingStartDTO.class);
		long channelId = data.getD().get("channelId");
		Session userSession = sessions.get(session.getId());

		if (!userSession.isAuthenticated()) return;

		List<Long> recipients = channelService.getById(channelId).getMembers().stream().map(Member::getId).toList();

		gatewayService.sendMessageToSpecificSessions(recipients, GatewayConstant.Opcode.DISPATCH.ordinal(), new TypingStartDTO(channelId, userSession.getUserId(), System.currentTimeMillis()), GatewayConstant.Event.TYPING_START.getValue());
	}
}
