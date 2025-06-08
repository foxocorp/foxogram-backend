package su.foxochat.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxochat.constant.ExceptionConstant;
import su.foxochat.exception.BaseException;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserContactNotFoundException extends BaseException {

	public UserContactNotFoundException() {
		super(ExceptionConstant.Messages.USER_CONTACT_NOT_FOUND.getValue(), UserContactNotFoundException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.User.CONTACT_NOT_FOUND.getValue());
	}
}
