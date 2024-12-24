package su.foxogram.exceptions.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;
import su.foxogram.exceptions.BaseException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserCredentialsIsInvalidException extends BaseException {

	public UserCredentialsIsInvalidException() {
		super(ExceptionsConstants.Messages.USER_CREDENTIALS_IS_INVALID.getValue(), UserCredentialsIsInvalidException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.User.CREDENTIALS_IS_INVALID.getValue());
	}
}