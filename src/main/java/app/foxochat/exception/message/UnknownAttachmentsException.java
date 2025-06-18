package app.foxochat.exception.message;

import app.foxochat.constant.ExceptionConstant;
import app.foxochat.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnknownAttachmentsException extends BaseException {

	public UnknownAttachmentsException() {
		super(ExceptionConstant.Messages.UNKNOWN_ATTACHMENTS.getValue(), UnknownAttachmentsException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.Message.UNKNOWN_ATTACHMENTS.getValue());
	}
}
