package su.foxogram.exceptions.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;
import su.foxogram.exceptions.BaseException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends BaseException {

	public UserNotFoundException() {
		super("Unknown user.", UserNotFoundException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.User.NOT_FOUND.getValue());
	}
}