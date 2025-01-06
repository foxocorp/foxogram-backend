package su.foxogram.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;
import su.foxogram.dtos.gateway.KafkaDTO;
import su.foxogram.services.WebSocketService;

import java.util.List;

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
		log.info(record.value());
		KafkaDTO dto = objectMapper.readValue(record.value(), KafkaDTO.class);

		int opcode = dto.getOpcode();
		List<Long> recipients = dto.getRecipients();
		Object data = dto.getData();

		log.info("{} - {}", opcode, data.toString());

		String message = objectMapper.writeValueAsString(new KafkaDTO(opcode, null, data, false));

		webSocketService.sendMessageToSessions(recipients, message);
	}
}
