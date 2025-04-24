package su.foxogram.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import su.foxogram.configs.APIConfig;
import su.foxogram.exceptions.otp.OTPExpiredException;
import su.foxogram.exceptions.otp.OTPsInvalidException;
import su.foxogram.models.OTP;
import su.foxogram.repositories.OTPRepository;

@Slf4j
@Service
public class OTPService {

	private final OTPRepository OTPRepository;

	private final APIConfig apiConfig;

	public OTPService(OTPRepository OTPRepository, APIConfig apiConfig) {
		this.OTPRepository = OTPRepository;
		this.apiConfig = apiConfig;
	}

	public OTP validateCode(String pathCode) throws OTPsInvalidException, OTPExpiredException {
		if (apiConfig.isDevelopment()) return null;

		OTP OTP = OTPRepository.findByValue(pathCode);

		if (OTP == null)
			throw new OTPsInvalidException();

		if (OTP.expiresAt <= System.currentTimeMillis())
			throw new OTPExpiredException();

		log.info("OTP ({}) for user ({}) validated successfully", OTP.getValue(), OTP.getUserId());

		return OTP;
	}

	public void delete(OTP OTP) {
		if (!apiConfig.isDevelopment()) OTPRepository.delete(OTP);
		log.info("OTP ({}, {}) deleted successfully", OTP.getValue(), OTP.getUserId());
	}

	public void save(long id, String type, String digitCode, long issuedAt, long expiresAt) {
		OTP OTP = new OTP(id, type, digitCode, issuedAt, expiresAt);
		if (!apiConfig.isDevelopment()) OTPRepository.save(OTP);
		log.info("OTP ({}, {}) saved successfully", OTP.getValue(), OTP.getUserId());
	}
}
