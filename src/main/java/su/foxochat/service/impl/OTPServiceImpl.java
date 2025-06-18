package su.foxochat.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import su.foxochat.config.APIConfig;
import su.foxochat.exception.otp.OTPExpiredException;
import su.foxochat.exception.otp.OTPsInvalidException;
import su.foxochat.model.OTP;
import su.foxochat.repository.OTPRepository;

@Slf4j
@Service
public class OTPServiceImpl implements su.foxochat.service.OTPService {

	private final OTPRepository otpRepository;

	private final APIConfig apiConfig;

	public OTPServiceImpl(OTPRepository otpRepository, APIConfig apiConfig) {
		this.apiConfig = apiConfig;
		this.otpRepository = otpRepository;
	}

	@Override
	public OTP validate(String pathCode) throws OTPsInvalidException, OTPExpiredException {

		if (apiConfig.isDevelopment()) return null;

		OTP OTP = otpRepository.findByValue(pathCode).orElseThrow(OTPsInvalidException::new);

		if (OTP.expiresAt <= System.currentTimeMillis())
			throw new OTPExpiredException();

		log.debug("OTP ({}) for user ({}) validated successfully", OTP.getValue(), OTP.getUserId());

		return OTP;
	}

	@Override
	public void delete(OTP OTP) {
		if (!apiConfig.isDevelopment()) {
			otpRepository.delete(OTP);
			log.debug("OTP ({}, {}) deleted successfully", OTP.getValue(), OTP.getUserId());
		}
	}

	@Override
	public void save(long id, String type, String digitCode, long issuedAt, long expiresAt) {
		OTP OTP = new OTP(id, type, digitCode, issuedAt, expiresAt);
		otpRepository.save(OTP);
		log.debug("OTP ({}, {}) saved successfully", OTP.getValue(), OTP.getUserId());
	}

	@Override
	public OTP getByUserId(long userId) throws OTPsInvalidException {
		return otpRepository.findByUserId(userId).orElseThrow(OTPsInvalidException::new);
	}
}
