package su.foxochat.exception.otp;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxochat.constant.ExceptionConstant;
import su.foxochat.exception.BaseException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OTPExpiredException extends BaseException {

	public OTPExpiredException() {
		super(ExceptionConstant.Messages.OTP_EXPIRED.getValue(), OTPExpiredException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.OTP.EXPIRED.getValue());
	}
}
