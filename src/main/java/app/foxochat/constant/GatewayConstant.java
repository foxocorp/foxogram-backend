package app.foxochat.constant;

public class GatewayConstant {

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
		USER_STATUS_UPDATE("USER_STATUS_UPDATE"),
		USER_UPDATE("USER_UPDATE"),
		CONTACT_ADD("CONTACT_ADD"),
		CONTACT_DELETE("CONTACT_DELETE"),
		TYPING_START("TYPING_START");

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
