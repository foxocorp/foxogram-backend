package su.foxochat.exception.message;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxochat.constant.ExceptionConstant;
import su.foxochat.exception.BaseException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnknownAttachmentsException extends BaseException {

	public UnknownAttachmentsException() {
		super(ExceptionConstant.Messages.UNKNOWN_ATTACHMENTS.getValue(), UnknownAttachmentsException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.Message.UNKNOWN_ATTACHMENTS.getValue());
	}
}
