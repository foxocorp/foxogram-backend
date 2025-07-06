package app.foxochat.service;

import app.foxochat.model.User;

import javax.crypto.SecretKey;

public interface JwtService {
    String generate(User user);

    SecretKey getSigningKey();
}
