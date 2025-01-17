package su.foxogram.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su.foxogram.configs.RabbitConfig;
import su.foxogram.dtos.gateway.RabbitDTO;

import java.util.List;

@Slf4j
@Service
public class RabbitService {

	private final RabbitTemplate rabbitTemplate;

	private final RabbitConfig rabbitConfig;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	public RabbitService(RabbitTemplate rabbitTemplate, RabbitConfig rabbitConfig) {
		this.rabbitTemplate = rabbitTemplate;
		this.rabbitConfig = rabbitConfig;
	}

	public void send(List<Long> recipients, Object data, String event) throws JsonProcessingException {
		rabbitTemplate.convertAndSend(rabbitConfig.getQueue(), objectMapper.writeValueAsString(new RabbitDTO(recipients, data, event)));
	}
}
