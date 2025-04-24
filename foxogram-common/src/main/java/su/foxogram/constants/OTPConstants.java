package su.foxogram.constants;

public class OTPConstants {
	public enum Lifetime {
		BASE(3600000),
		RESEND(60000);

		private final long time;

		Lifetime(long time) {
			this.time = time;
		}

		public long getValue() {
			return time;
		}
	}
}
