package su.foxogram.exceptions.message;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;
import su.foxogram.exceptions.BaseException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AttachmentsCannotBeEmpty extends BaseException {

	public AttachmentsCannotBeEmpty() {
		super(ExceptionsConstants.Messages.ATTACHMENTS_CANNOT_BE_EMPTY.getValue(), AttachmentsCannotBeEmpty.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Message.ATTACHMENTS_CANNOT_BE_EMPTY.getValue());
	}
}
