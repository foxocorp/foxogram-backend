package su.foxogram.constant;

import lombok.Getter;

public class UserConstant {

	@Getter
	public enum Flags {
		AWAITING_CONFIRMATION(1),
		EMAIL_VERIFIED(1 << 1),
		DISABLED(1 << 2);

		private final long bit;

		Flags(long bit) {
			this.bit = bit;
		}
	}

	@Getter
	public enum Type {
		USER(1),
		BOT(2);

		private final int type;

		Type(int type) {
			this.type = type;
		}
	}

	@Getter
	public enum Status {
		ONLINE(1),
		OFFLINE(2);

		private final int status;

		Status(int status) {
			this.status = status;
		}
	}
}
