package su.foxogram.dtos.gateway;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class KafkaDTO {

	private List<Long> recipients;

	private Object data;

	private String type;

	public KafkaDTO() {
	}

	public KafkaDTO(List<Long> recipients, Object data, String type) {
		this.recipients = recipients;
		this.data = data;
		this.type = type;
	}
}