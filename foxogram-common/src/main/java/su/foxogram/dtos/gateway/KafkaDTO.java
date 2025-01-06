package su.foxogram.dtos.gateway;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class KafkaDTO {

	private int opcode;

	private List<Long> recipients;

	private Object data;

	public KafkaDTO() {
	}

	public KafkaDTO(int opcode, List<Long> recipients, Object data, boolean includeRecipients) {
		this.opcode = opcode;
		if (includeRecipients) {
			this.recipients = recipients;
		}
		this.data = data;
	}
}