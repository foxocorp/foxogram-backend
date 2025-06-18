package app.foxochat.constant;

import lombok.Getter;

public class MemberConstant {

	@Getter
	public enum Permissions {
		ADMIN(1),
		BAN_MEMBERS(1 << 1),
		KICK_MEMBERS(1 << 2),
		MANAGE_MESSAGES(1 << 3),
		MANAGE_CHANNEL(1 << 4),
		ATTACH_FILES(1 << 5),
		SEND_MESSAGES(1 << 6);

		private final long bit;

		Permissions(long bit) {
			this.bit = bit;
		}

	}
}
