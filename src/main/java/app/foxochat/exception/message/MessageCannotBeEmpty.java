package app.foxochat.exception.message;

import app.foxochat.constant.ExceptionConstant;
import app.foxochat.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MessageCannotBeEmpty extends BaseException {

	public MessageCannotBeEmpty() {
		super(ExceptionConstant.Messages.MESSAGE_CANNOT_BE_EMPTY.getValue(), MessageCannotBeEmpty.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.Message.CANNOT_BE_EMPTY.getValue());
	}
}
