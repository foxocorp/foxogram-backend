package su.foxogram.exception.otp;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constant.ExceptionConstant;
import su.foxogram.exception.BaseException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OTPExpiredException extends BaseException {

	public OTPExpiredException() {
		super(ExceptionConstant.Messages.OTP_EXPIRED.getValue(), OTPExpiredException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.OTP.EXPIRED.getValue());
	}
}
