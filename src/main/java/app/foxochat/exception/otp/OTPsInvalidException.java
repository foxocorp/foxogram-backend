package app.foxochat.exception.otp;

import app.foxochat.constant.ExceptionConstant;
import app.foxochat.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OTPsInvalidException extends BaseException {

	public OTPsInvalidException() {
		super(ExceptionConstant.Messages.OTP_IS_INVALID.getValue(), OTPsInvalidException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.OTP.IS_INVALID.getValue());
	}
}
