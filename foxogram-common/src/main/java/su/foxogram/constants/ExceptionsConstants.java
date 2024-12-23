package su.foxogram.constants;

public class ExceptionsConstants {
	private static final int USER_ERROR = 100;

	private static final int CHANNEL_ERROR = 200;

	private static final int MEMBER_ERROR = 300;

	private static final int MESSAGE_ERROR = 400;

	private static final int CODE_ERROR = 500;

	private static final int CDN_ERROR = 700;

	private static final int API_ERROR = 800;

	private static final int UNKNOWN_ERROR = 900;

	public enum User {
		NOT_FOUND,
		EMAIL_NOT_VERIFIED,
		CREDENTIALS_DUPLICATE,
		CREDENTIALS_IS_INVALID,
		UNAUTHORIZED;

		public int getValue() {
			return USER_ERROR + this.ordinal();
		}
	}

	public enum Channel {
		NOT_FOUND,
		ALREADY_EXIST;

		public int getValue() {
			return CHANNEL_ERROR + this.ordinal();
		}
	}

	public enum Member {
		NOT_FOUND,
		ALREADY_EXIST,
		MISSING_PERMISSIONS;

		public int getValue() {
			return MEMBER_ERROR + this.ordinal();
		}
	}

	public enum Message {
		NOT_FOUND;

		public int getValue() {
			return MESSAGE_ERROR + this.ordinal();
		}
	}

	public enum Code {
		IS_INVALID,
		EXPIRED,
		WAIT_TO_RESEND;

		public int getValue() {
			return CODE_ERROR + this.ordinal();
		}
	}

	public enum CDN {
		UPLOAD_FAILED;

		public int getValue() {
			return CDN_ERROR + this.ordinal();
		}
	}

	public enum API {
		RATE_LIMIT_EXCEEDED,
		EMPTY_BODY,
		VALIDATION_ERROR;

		public int getValue() {
			return API_ERROR + this.ordinal();
		}
	}

	public enum Unknown {
		ERROR;

		public int getValue() {
			return UNKNOWN_ERROR + this.ordinal();
		}
	}
}
