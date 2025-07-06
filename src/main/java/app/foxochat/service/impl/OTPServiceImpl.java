package app.foxochat.service.impl;

import app.foxochat.config.APIConfig;
import app.foxochat.exception.otp.OTPExpiredException;
import app.foxochat.exception.otp.OTPsInvalidException;
import app.foxochat.model.OTP;
import app.foxochat.repository.OTPRepository;
import app.foxochat.service.OTPService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OTPServiceImpl implements OTPService {

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
