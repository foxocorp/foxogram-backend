package su.foxogram.dto.gateway;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RabbitDTO {

	private List<Long> recipients;

	private Object data;

	private String type;

	public RabbitDTO() {
	}

	public RabbitDTO(List<Long> recipients, Object data, String type) {
		this.recipients = recipients;
		this.data = data;
		this.type = type;
	}
}
