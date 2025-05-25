package su.foxogram.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import su.foxogram.config.RabbitConfig;
import su.foxogram.dto.gateway.StatusDTO;

@Slf4j
@Service
public class RabbitService {

	private final RabbitTemplate rabbitTemplate;

	private final RabbitConfig rabbitConfig;

	private final ObjectMapper objectMapper;

	public RabbitService(RabbitTemplate rabbitTemplate, RabbitConfig rabbitConfig, ObjectMapper objectMapper) {
		this.rabbitTemplate = rabbitTemplate;
		this.rabbitConfig = rabbitConfig;
		this.objectMapper = objectMapper;
	}

	public void send(long userId, int status) throws JsonProcessingException {
		rabbitTemplate.convertAndSend(rabbitConfig.getQueue(), objectMapper.writeValueAsString(new StatusDTO(userId, status)));
		log.debug("Sent message to rabbit queue {} with user id {} and status {}", rabbitConfig.getQueue(), userId, status);
	}
}
