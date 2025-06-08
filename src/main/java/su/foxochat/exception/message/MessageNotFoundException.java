package su.foxochat.exception.message;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxochat.constant.ExceptionConstant;
import su.foxochat.exception.BaseException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MessageNotFoundException extends BaseException {

	public MessageNotFoundException() {
		super(ExceptionConstant.Messages.MESSAGE_NOT_FOUND.getValue(), MessageNotFoundException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.Message.NOT_FOUND.getValue());
	}
}
