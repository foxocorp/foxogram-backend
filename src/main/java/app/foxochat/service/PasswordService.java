package app.foxochat.service;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public interface PasswordService {
    String hash(String password);

    boolean verify(String password, String hashedPassword);
}
