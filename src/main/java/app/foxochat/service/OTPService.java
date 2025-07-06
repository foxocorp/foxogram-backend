package app.foxochat.service;

import app.foxochat.exception.otp.OTPExpiredException;
import app.foxochat.exception.otp.OTPsInvalidException;
import app.foxochat.model.OTP;

public interface OTPService {

    OTP validate(String pathCode) throws OTPsInvalidException, OTPExpiredException;

    void delete(OTP OTP);

    void save(long id, String type, String digitCode, long issuedAt, long expiresAt);

    OTP getByUserId(long userId) throws OTPsInvalidException;
}
