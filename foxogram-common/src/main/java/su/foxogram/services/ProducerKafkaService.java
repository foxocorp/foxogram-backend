package su.foxogram.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import su.foxogram.configs.KafkaConfig;
import su.foxogram.dtos.gateway.KafkaDTO;

import java.util.List;

@Slf4j
@Service
public class ProducerKafkaService {

	private final String topic;

	private final KafkaTemplate<String, String> kafkaTemplate;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	public ProducerKafkaService(KafkaTemplate<String, String> kafkaTemplate, KafkaConfig kafkaConfig) {
		this.kafkaTemplate = kafkaTemplate;
		this.topic = kafkaConfig.getTopic();
	}

	public void send(int opcode, List<Long> recipients, Object data) throws JsonProcessingException {
		kafkaTemplate.send(topic, objectMapper.writeValueAsString(new KafkaDTO(opcode, recipients, data, true)));
	}
}
