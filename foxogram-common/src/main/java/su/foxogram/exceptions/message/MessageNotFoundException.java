package su.foxogram.exceptions.message;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;
import su.foxogram.exceptions.BaseException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MessageNotFoundException extends BaseException {

	public MessageNotFoundException() {
		super("Unable to find message(s) for this channel or matching these parameters", MessageNotFoundException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Message.NOT_FOUND.getValue());
	}
}