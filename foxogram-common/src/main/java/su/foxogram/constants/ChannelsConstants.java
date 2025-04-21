package su.foxogram.constants;

import lombok.Getter;

public class ChannelsConstants {
	@Getter
	public enum Type {
		DM(1),
		GROUP(2),
		CHANNEL(3);

		private final int type;

		Type(int type) {
			this.type = type;
		}
	}

	@Getter
	public enum Flags {
		PUBLIC(1),
		BLOCKED(1 << 1);

		private final long bit;

		Flags(long bit) {
			this.bit = bit;
		}
	}
}
