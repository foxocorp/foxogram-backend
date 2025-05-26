package su.foxogram.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constant.ExceptionConstant;
import su.foxogram.exception.BaseException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends BaseException {

	public UserNotFoundException() {
		super(ExceptionConstant.Messages.USER_NOT_FOUND.getValue(), UserNotFoundException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.User.NOT_FOUND.getValue());
	}
}
