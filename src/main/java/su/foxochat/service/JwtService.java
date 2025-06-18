package su.foxochat.service;

import su.foxochat.model.User;

import javax.crypto.SecretKey;

public interface JwtService {
	String generate(User user);

	SecretKey getSigningKey();
}
