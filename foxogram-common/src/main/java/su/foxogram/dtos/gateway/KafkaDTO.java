package su.foxogram.dtos.gateway;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class KafkaDTO {

	private int op;

	private List<Long> recipients;

	private Object d;

	public KafkaDTO() {
	}

	public KafkaDTO(int opcode, List<Long> recipients, Object data, boolean includeRecipients) {
		this.op = opcode;
		if (includeRecipients) {
			this.recipients = recipients;
		}
		this.d = data;
	}
}