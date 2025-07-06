package app.foxochat.exception.otp;

import app.foxochat.constant.ExceptionConstant;
import app.foxochat.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OTPExpiredException extends BaseException {

    public OTPExpiredException() {
        super(
                ExceptionConstant.Messages.OTP_EXPIRED.getValue(),
                OTPExpiredException.class.getAnnotation(ResponseStatus.class).value(),
                ExceptionConstant.OTP.EXPIRED.getValue()
        );
    }
}
