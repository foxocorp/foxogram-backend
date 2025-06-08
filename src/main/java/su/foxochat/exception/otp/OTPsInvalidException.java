package su.foxochat.exception.otp;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxochat.constant.ExceptionConstant;
import su.foxochat.exception.BaseException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OTPsInvalidException extends BaseException {

	public OTPsInvalidException() {
		super(ExceptionConstant.Messages.OTP_IS_INVALID.getValue(), OTPsInvalidException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.OTP.IS_INVALID.getValue());
	}
}
