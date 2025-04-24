package su.foxogram.exceptions.otp;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;
import su.foxogram.exceptions.BaseException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OTPsInvalidException extends BaseException {

	public OTPsInvalidException() {
		super(ExceptionsConstants.Messages.OTP_IS_INVALID.getValue(), OTPsInvalidException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.OTP.IS_INVALID.getValue());
	}
}