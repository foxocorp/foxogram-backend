package app.foxochat.exception.user;

import app.foxochat.constant.ExceptionConstant;
import app.foxochat.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserCredentialsIsInvalidException extends BaseException {

	public UserCredentialsIsInvalidException() {
		super(ExceptionConstant.Messages.USER_CREDENTIALS_IS_INVALID.getValue(), UserCredentialsIsInvalidException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.User.CREDENTIALS_IS_INVALID.getValue());
	}
}
