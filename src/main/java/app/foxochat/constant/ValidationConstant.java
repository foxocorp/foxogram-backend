package app.foxochat.constant;

public class ValidationConstant {

    public static class Lengths {
        public static final int MIN = 4;

        public static final int PASSWORD = 128;

        public static final int DISPLAY_NAME = 32;

        public static final int BIO = 64;

        public static final int USERNAME = 32;

        public static final int EMAIL = 64;

        public static final int CHANNEL_NAME = 16;

        public static final int MESSAGE_CONTENT = 5000;

        public static final int ATTACHMENTS_MAX = 10;

        public static final int FILENAME = 128;

        public static final int CONTENT_TYPE = 16;
    }

    public static class Messages {
        public static final String WRONG_LENGTH = " must be between {min} and {max} characters long";

        public static final String INCORRECT = " has incorrect format";

        public static final String MUST_BE_POSITIVE_OR_ZERO = " must be positive or zero";

        public static final String MUST_NOT_BE_NULL = " must not be null";

        public static final String ATTACHMENTS_WRONG_SIZE = "Message can contain only {max} attachments";
    }

    public static class Regex {
        public static final String EMAIL_REGEX = "^[\\w+.-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$";

        public static final String NAME_REGEX = "^[A-Za-z0-9_-]+$";
    }
}
