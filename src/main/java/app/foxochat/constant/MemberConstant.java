package app.foxochat.constant;

import lombok.Getter;

public class MemberConstant {

	@Getter
	public enum Permissions {
		OWNER(1),
		ADMIN(1 << 1),
		BAN_MEMBERS(1 << 2),
		KICK_MEMBERS(1 << 3),
		MANAGE_MESSAGES(1 << 4),
		MANAGE_CHANNEL(1 << 5),
		ATTACH_FILES(1 << 6),
		SEND_MESSAGES(1 << 7);

		private final long bit;

		Permissions(long bit) {
			this.bit = bit;
		}

	}
}
