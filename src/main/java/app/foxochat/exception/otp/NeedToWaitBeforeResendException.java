package app.foxochat.exception.otp;

import app.foxochat.constant.ExceptionConstant;
import app.foxochat.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NeedToWaitBeforeResendException extends BaseException {

	public NeedToWaitBeforeResendException() {
		super(ExceptionConstant.Messages.NEED_TO_WAIT.getValue(), NeedToWaitBeforeResendException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.OTP.WAIT_TO_RESEND.getValue());
	}
}
