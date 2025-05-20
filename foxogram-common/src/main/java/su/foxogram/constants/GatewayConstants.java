package su.foxogram.constants;

public class GatewayConstants {

	public static final int HEARTBEAT_INTERVAL = 30000;

	public static final int HEARTBEAT_TIMEOUT = 3;

	public enum Event {
		MESSAGE_CREATE("MESSAGE_CREATE"),
		MESSAGE_UPDATE("MESSAGE_UPDATE"),
		MESSAGE_DELETE("MESSAGE_DELETE"),
		CHANNEL_CREATE("CHANNEL_CREATE"),
		CHANNEL_UPDATE("CHANNEL_UPDATE"),
		CHANNEL_DELETE("CHANNEL_DELETE"),
		MEMBER_ADD("MEMBER_ADD"),
		MEMBER_REMOVE("MEMBER_REMOVE"),
		USER_UPDATE("USER_UPDATE");

		private final String name;

		Event(String name) {
			this.name = name;
		}

		public String getValue() {
			return name;
		}
	}

	public enum Opcode {
		DISPATCH, // 0
		IDENTIFY, // 1
		HELLO, // 2
		HEARTBEAT, // 3
		HEARTBEAT_ACK, // 4
	}
}
