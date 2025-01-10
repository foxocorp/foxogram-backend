package su.foxogram.listeners;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;
import su.foxogram.constants.GatewayConstants;
import su.foxogram.dtos.gateway.KafkaDTO;
import su.foxogram.services.WebSocketService;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class KafkaListener {

	private final WebSocketService webSocketService;

	private final String TOPIC = "events";

	private final ObjectMapper objectMapper = new ObjectMapper();

	public KafkaListener(WebSocketService webSocketService) {
		this.webSocketService = webSocketService;
	}

	@org.springframework.kafka.annotation.KafkaListener(topicPattern = TOPIC)
	public void listen(ConsumerRecord<String, String> record) throws Exception {
		KafkaDTO dto = objectMapper.readValue(record.value(), KafkaDTO.class);

		int opcode = GatewayConstants.Opcode.DISPATCH.ordinal();
		List<Long> recipients = dto.getRecipients();
		String type = dto.getType();
		Map<String, Object> data = objectMapper.convertValue(dto.getData(), new TypeReference<>() {
		});

		webSocketService.sendMessageToSessions(recipients, opcode, data, type);
	}
}
