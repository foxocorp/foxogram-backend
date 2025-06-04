package su.foxogram.service;

import su.foxogram.model.User;

import javax.crypto.SecretKey;

public interface JwtService {
	String generate(User user);

	SecretKey getSigningKey(int tokenVersion);
}
