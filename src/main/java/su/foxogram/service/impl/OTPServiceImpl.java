package su.foxogram.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import su.foxogram.exception.otp.OTPExpiredException;
import su.foxogram.exception.otp.OTPsInvalidException;
import su.foxogram.model.OTP;
import su.foxogram.repository.OTPRepository;

@Slf4j
@Service
public class OTPServiceImpl implements su.foxogram.service.OTPService {

	private final OTPRepository otpRepository;

	public OTPServiceImpl(OTPRepository otpRepository) {
		this.otpRepository = otpRepository;
	}

	@Override
	public OTP validate(String pathCode) throws OTPsInvalidException, OTPExpiredException {

		OTP OTP = otpRepository.findByValue(pathCode).orElseThrow(OTPsInvalidException::new);

		if (OTP.expiresAt <= System.currentTimeMillis())
			throw new OTPExpiredException();

		log.debug("OTP ({}) for user ({}) validated successfully", OTP.getValue(), OTP.getUserId());

		return OTP;
	}

	@Override
	public void delete(OTP OTP) {
		otpRepository.delete(OTP);
		log.debug("OTP ({}, {}) deleted successfully", OTP.getValue(), OTP.getUserId());
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
