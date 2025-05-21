package su.foxogram.dto.gateway;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusDTO {

	private long userId;

	private int status;

	public StatusDTO() {

	}

	public StatusDTO(long userId, int status) {
		this.userId = userId;
		this.status = status;
	}
}
