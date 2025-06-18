package app.foxochat.exception.user;

import app.foxochat.constant.ExceptionConstant;
import app.foxochat.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends BaseException {

	public UserNotFoundException() {
		super(ExceptionConstant.Messages.USER_NOT_FOUND.getValue(), UserNotFoundException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.User.NOT_FOUND.getValue());
	}
}
