package app.foxochat.service;

import org.springframework.scheduling.annotation.Async;

public interface EmailService {
    @Async
    void send(String to, long id, String type, String username, String digitCode, long issuedAt, long expiresAt,
              String token);
}
