package app.foxochat.exception.message;

import app.foxochat.constant.ExceptionConstant;
import app.foxochat.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MessageNotFoundException extends BaseException {

	public MessageNotFoundException() {
		super(ExceptionConstant.Messages.MESSAGE_NOT_FOUND.getValue(), MessageNotFoundException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.Message.NOT_FOUND.getValue());
	}
}
