package su.foxogram.service;

import javax.crypto.SecretKey;

public interface JwtService {
	String generate(long id, String passwordHash);

	SecretKey getSigningKey();
}
