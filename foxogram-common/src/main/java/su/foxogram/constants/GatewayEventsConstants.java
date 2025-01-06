package su.foxogram.constants;

public class GatewayEventsConstants {

	private final static int AUTH_EVENT = 0;

	private final static int USER_EVENT = 10;

	private final static int CHANNEL_EVENT = 20;

	private final static int MEMBER_EVENT = 30;

	private final static int MESSAGE_EVENT = 40;

	private final static int COMMON_EVENT = 90;

	public enum Auth {
		HELLO,
		OK;

		public int getValue() {
			return AUTH_EVENT + this.ordinal();
		}
	}

	public enum User {
		UPDATED,
		DELETED;

		public int getValue() {
			return USER_EVENT + this.ordinal();
		}
	}

	public enum Member {
		CREATED,
		UPDATED,
		DELETED;

		public int getValue() {
			return MEMBER_EVENT + this.ordinal();
		}
	}

	public enum Channel {
		UPDATED,
		DELETED;

		public int getValue() {
			return CHANNEL_EVENT + this.ordinal();
		}
	}

	public enum Message {
		CREATED,
		UPDATED,
		DELETED;

		public int getValue() {
			return MESSAGE_EVENT + this.ordinal();
		}
	}

	public enum Common {
		EXCEPTION;

		public int getValue() {
			return COMMON_EVENT + this.ordinal();
		}
	}
}
