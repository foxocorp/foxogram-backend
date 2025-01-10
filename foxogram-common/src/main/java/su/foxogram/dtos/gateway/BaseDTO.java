package su.foxogram.dtos.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;

public interface BaseDTO {

	ObjectMapper objectMapper = new ObjectMapper();

	default TextMessage getMessage() throws JsonProcessingException {
		return new TextMessage(objectMapper.writeValueAsString(this));
	}
}
