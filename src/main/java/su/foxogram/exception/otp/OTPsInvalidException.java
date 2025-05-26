package su.foxogram.exception.otp;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constant.ExceptionConstant;
import su.foxogram.exception.BaseException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OTPsInvalidException extends BaseException {

	public OTPsInvalidException() {
		super(ExceptionConstant.Messages.OTP_IS_INVALID.getValue(), OTPsInvalidException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.OTP.IS_INVALID.getValue());
	}
}
