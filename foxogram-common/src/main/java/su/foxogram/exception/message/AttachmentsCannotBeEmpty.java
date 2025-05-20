package su.foxogram.exception.message;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constant.ExceptionConstant;
import su.foxogram.exception.BaseException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AttachmentsCannotBeEmpty extends BaseException {

	public AttachmentsCannotBeEmpty() {
		super(ExceptionConstant.Messages.ATTACHMENTS_CANNOT_BE_EMPTY.getValue(), AttachmentsCannotBeEmpty.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.Message.ATTACHMENTS_CANNOT_BE_EMPTY.getValue());
	}
}
