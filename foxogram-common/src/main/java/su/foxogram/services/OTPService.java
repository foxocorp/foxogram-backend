package su.foxogram.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import su.foxogram.exceptions.otp.OTPExpiredException;
import su.foxogram.exceptions.otp.OTPsInvalidException;
import su.foxogram.models.OTP;
import su.foxogram.repositories.OTPRepository;

@Slf4j
@Service
public class OTPService {

	private final OTPRepository otpRepository;

	public OTPService(OTPRepository otpRepository) {
		this.otpRepository = otpRepository;
	}

	public OTP validateCode(String pathCode) throws OTPsInvalidException, OTPExpiredException {

		OTP OTP = otpRepository.findByValue(pathCode).orElseThrow(OTPsInvalidException::new);

		if (OTP.expiresAt <= System.currentTimeMillis())
			throw new OTPExpiredException();

		log.debug("OTP ({}) for user ({}) validated successfully", OTP.getValue(), OTP.getUserId());

		return OTP;
	}

	public void delete(OTP OTP) {
		otpRepository.delete(OTP);
		log.debug("OTP ({}, {}) deleted successfully", OTP.getValue(), OTP.getUserId());
	}

	public void save(long id, String type, String digitCode, long issuedAt, long expiresAt) {
		OTP OTP = new OTP(id, type, digitCode, issuedAt, expiresAt);
		otpRepository.save(OTP);
		log.debug("OTP ({}, {}) saved successfully", OTP.getValue(), OTP.getUserId());
	}

	public OTP getByUserId(long userId) throws OTPsInvalidException {
		return otpRepository.findByUserId(userId).orElseThrow(OTPsInvalidException::new);
	}
}
