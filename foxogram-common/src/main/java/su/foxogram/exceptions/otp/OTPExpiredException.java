package su.foxogram.exceptions.otp;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;
import su.foxogram.exceptions.BaseException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OTPExpiredException extends BaseException {

	public OTPExpiredException() {
		super(ExceptionsConstants.Messages.OTP_EXPIRED.getValue(), OTPExpiredException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.OTP.EXPIRED.getValue());
	}
}