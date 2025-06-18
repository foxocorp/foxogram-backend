package app.foxochat.util;

import org.mindrot.jbcrypt.BCrypt;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class PasswordHasher {

	public static String hashPassword(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt());
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean verifyPassword(String password, String hashedPassword) {
		return BCrypt.checkpw(password, hashedPassword);
	}
}
