package su.foxochat.service;

import su.foxochat.exception.otp.OTPExpiredException;
import su.foxochat.exception.otp.OTPsInvalidException;
import su.foxochat.model.OTP;

public interface OTPService {

	OTP validate(String pathCode) throws OTPsInvalidException, OTPExpiredException;

	void delete(OTP OTP);

	void save(long id, String type, String digitCode, long issuedAt, long expiresAt);

	OTP getByUserId(long userId) throws OTPsInvalidException;
}
