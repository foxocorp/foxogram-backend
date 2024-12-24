package su.foxogram.exceptions.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;
import su.foxogram.exceptions.BaseException;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserCredentialsDuplicateException extends BaseException {

	public UserCredentialsDuplicateException() {
		super(ExceptionsConstants.Messages.USER_CREDENTIALS_DUPLICATE.getValue(), UserCredentialsDuplicateException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.User.CREDENTIALS_DUPLICATE.getValue());
	}
}
