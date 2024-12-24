package su.foxogram.exceptions.code;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;
import su.foxogram.exceptions.BaseException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NeedToWaitBeforeResendException extends BaseException {

	public NeedToWaitBeforeResendException() {
		super(ExceptionsConstants.Messages.NEED_TO_WAIT.getValue(), NeedToWaitBeforeResendException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Code.WAIT_TO_RESEND.getValue());
	}
}