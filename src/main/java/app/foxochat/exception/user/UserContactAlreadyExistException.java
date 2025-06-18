package app.foxochat.exception.user;

import app.foxochat.constant.ExceptionConstant;
import app.foxochat.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserContactAlreadyExistException extends BaseException {

	public UserContactAlreadyExistException() {
		super(ExceptionConstant.Messages.USER_CONTACT_ALREADY_EXIST.getValue(), UserContactAlreadyExistException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.User.CONTACT_ALREADY_EXIST.getValue());
	}
}
