package su.foxogram.exceptions.message;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;
import su.foxogram.exceptions.BaseException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnknownAttachmentsException extends BaseException {

	public UnknownAttachmentsException() {
		super(ExceptionsConstants.Messages.UNKNOWN_ATTACHMENTS.getValue(), UnknownAttachmentsException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Message.UNKNOWN_ATTACHMENTS.getValue());
	}
}
