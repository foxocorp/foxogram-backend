package su.foxogram.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import su.foxogram.dto.gateway.StatusDTO;
import su.foxogram.service.UserService;

@Slf4j
@Component
public class RabbitListener {

	private final ObjectMapper objectMapper;

	private final UserService userService;

	@Autowired
	public RabbitListener(ObjectMapper objectMapper, UserService userService) {
		this.objectMapper = objectMapper;
		this.userService = userService;
	}

	@org.springframework.amqp.rabbit.annotation.RabbitListener(queues = "${rabbit.queue}")
	public void listen(String in) throws Exception {
		StatusDTO dto = objectMapper.readValue(in, StatusDTO.class);

		long userId = dto.getUserId();
		int status = dto.getStatus();

		if (userId == 0 || status == 0) return;

		log.info("Got status message for {} with status {}", userId, status);
		userService.setStatus(userId, status);
	}
}
