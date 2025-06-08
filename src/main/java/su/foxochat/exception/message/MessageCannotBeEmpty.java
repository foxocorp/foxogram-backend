package su.foxochat.exception.message;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxochat.constant.ExceptionConstant;
import su.foxochat.exception.BaseException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MessageCannotBeEmpty extends BaseException {

	public MessageCannotBeEmpty() {
		super(ExceptionConstant.Messages.MESSAGE_CANNOT_BE_EMPTY.getValue(), MessageCannotBeEmpty.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.Message.CANNOT_BE_EMPTY.getValue());
	}
}
