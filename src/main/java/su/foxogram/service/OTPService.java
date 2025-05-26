package su.foxogram.service;

import su.foxogram.exception.otp.OTPExpiredException;
import su.foxogram.exception.otp.OTPsInvalidException;
import su.foxogram.model.OTP;

public interface OTPService {

	OTP validate(String pathCode) throws OTPsInvalidException, OTPExpiredException;

	void delete(OTP OTP);

	void save(long id, String type, String digitCode, long issuedAt, long expiresAt);

	OTP getByUserId(long userId) throws OTPsInvalidException;
}
