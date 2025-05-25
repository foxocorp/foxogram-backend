package su.foxogram.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constant.ExceptionConstant;
import su.foxogram.exception.BaseException;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserContactAlreadyExistException extends BaseException {

	public UserContactAlreadyExistException() {
		super(ExceptionConstant.Messages.USER_CONTACT_ALREADY_EXIST.getValue(), UserContactAlreadyExistException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.User.CONTACT_ALREADY_EXIST.getValue());
	}
}
