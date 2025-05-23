package su.foxogram.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import su.foxogram.constant.GatewayConstant;
import su.foxogram.dto.gateway.RabbitDTO;
import su.foxogram.service.WebSocketService;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RabbitListener {

	private final WebSocketService webSocketService;

	private final ObjectMapper objectMapper;

	@Autowired
	public RabbitListener(WebSocketService webSocketService, ObjectMapper objectMapper) {
		this.webSocketService = webSocketService;
		this.objectMapper = objectMapper;
	}

	@org.springframework.amqp.rabbit.annotation.RabbitListener(queues = "${rabbit.queue}")
	public void listen(String in) throws Exception {
		RabbitDTO dto = objectMapper.readValue(in, RabbitDTO.class);

		int opcode = GatewayConstant.Opcode.DISPATCH.ordinal();
		List<Long> recipients = dto.getRecipients();
		String type = dto.getType();
		Map<String, Object> data = objectMapper.convertValue(dto.getData(), new TypeReference<>() {
		});

		if (type == null || recipients == null) return;

		log.info("Got {} with opcode {} sent to {}", type, opcode, recipients);
		webSocketService.sendMessageToSessions(recipients, opcode, data, type);
	}
}
