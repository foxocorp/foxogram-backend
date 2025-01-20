package su.foxogram.exceptions.message;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;
import su.foxogram.exceptions.BaseException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MessageCannotBeEmpty extends BaseException {

	public MessageCannotBeEmpty() {
		super(ExceptionsConstants.Messages.MESSAGE_CANNOT_BE_EMPTY.getValue(), MessageCannotBeEmpty.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Message.CANNOT_BE_EMPTY.getValue());
	}
}
