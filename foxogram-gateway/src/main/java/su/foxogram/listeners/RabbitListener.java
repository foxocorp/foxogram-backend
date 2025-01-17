package su.foxogram.listeners;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import su.foxogram.constants.GatewayConstants;
import su.foxogram.dtos.gateway.RabbitDTO;
import su.foxogram.services.WebSocketService;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RabbitListener {

	private final WebSocketService webSocketService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public RabbitListener(WebSocketService webSocketService) {
		this.webSocketService = webSocketService;
	}

	@org.springframework.amqp.rabbit.annotation.RabbitListener(queues = "${rabbit.queue}")
	public void listen(String in) throws Exception {
		RabbitDTO dto = objectMapper.readValue(in, RabbitDTO.class);

		int opcode = GatewayConstants.Opcode.DISPATCH.ordinal();
		List<Long> recipients = dto.getRecipients();
		String type = dto.getType();
		Map<String, Object> data = objectMapper.convertValue(dto.getData(), new TypeReference<>() {
		});

		webSocketService.sendMessageToSessions(recipients, opcode, data, type);
	}
}
