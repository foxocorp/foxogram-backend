package app.foxochat.constant;

public class ExceptionConstant {

	private static final int USER_ERROR = 100;

	private static final int CHANNEL_ERROR = 200;

	private static final int MEMBER_ERROR = 300;

	private static final int MESSAGE_ERROR = 400;

	private static final int OTP_ERROR = 500;

	private static final int CDN_ERROR = 700;

	private static final int API_ERROR = 800;

	private static final int UNKNOWN_ERROR = 900;

	public enum Messages {
		SERVER_EXCEPTION("Server exception ({}, {}, {}) occurred"),
		INTERNAL_ERROR("An internal server error has occurred"),
		REQUEST_BODY_EMPTY("Request body cannot be empty"),
		SERVER_EXCEPTION_STACKTRACE("Server exception stacktrace:"),
		UPLOAD_FAILED("Image upload failed"),
		INVALID_FILE_FORMAT("Invalid file format"),
		CHANNEL_ALREADY_EXIST("Channel with this name already exist"),
		CHANNEL_NOT_FOUND("Unknown channel"),
		OTP_EXPIRED("OTP has expired"),
		OTP_IS_INVALID("OTP is invalid"),
		NEED_TO_WAIT("You need to wait 1 minute to resend OTP again"),
		MEMBER_ALREADY_EXIST("You've already joined this channel"),
		MEMBER_NOT_FOUND("Can't find member in this channel"),
		MISSING_PERMISSIONS("You don't have enough permissions to perform this action"),
		MESSAGE_NOT_FOUND("Unable to find message(s) for this channel or matching these parameters"),
		MESSAGE_CANNOT_BE_EMPTY("Message cannot be empty"),
		MEDIA_CANNOT_BE_EMPTY("Media cannot be empty"),
		UNKNOWN_MEDIA("Unknown media id(s)"),
		USER_CREDENTIALS_DUPLICATE("User with this username/email already exist"),
		USER_CREDENTIALS_IS_INVALID("Invalid password or email"),
		USER_EMAIL_VERIFIED("You need to verify your email first"),
		USER_NOT_FOUND("Unknown user"),
		USER_UNAUTHORIZED("You need to authorize first"),
		ROUTE_NOT_FOUND("Route not found"),
		USER_CONTACT_ALREADY_EXIST("Contact already exist"),
		USER_CONTACT_NOT_FOUND("Contact not found");

		private final String message;

		Messages(String message) {
			this.message = message;
		}

		public String getValue() {
			return message;
		}
	}

	public enum User {
		NOT_FOUND,
		EMAIL_NOT_VERIFIED,
		CREDENTIALS_DUPLICATE,
		CREDENTIALS_IS_INVALID,
		UNAUTHORIZED,
		CONTACT_ALREADY_EXIST,
		CONTACT_NOT_FOUND;

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
		NOT_FOUND,
		CANNOT_BE_EMPTY;

		public int getValue() {
			return MESSAGE_ERROR + this.ordinal();
		}
	}

	public enum OTP {
		IS_INVALID,
		EXPIRED,
		WAIT_TO_RESEND;

		public int getValue() {
			return OTP_ERROR + this.ordinal();
		}
	}

	public enum Media {
		UPLOAD_FAILED,
		INVALID_FILE_FORMAT,
		CANNOT_BE_EMPTY,
		UNKNOWN;

		public int getValue() {
			return CDN_ERROR + this.ordinal();
		}
	}

	public enum API {
		RATE_LIMIT_EXCEEDED,
		EMPTY_BODY,
		VALIDATION_ERROR,
		ROUTE_NOT_FOUND;

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
