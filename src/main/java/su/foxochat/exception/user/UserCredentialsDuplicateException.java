package su.foxochat.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxochat.constant.ExceptionConstant;
import su.foxochat.exception.BaseException;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserCredentialsDuplicateException extends BaseException {

	public UserCredentialsDuplicateException() {
		super(ExceptionConstant.Messages.USER_CREDENTIALS_DUPLICATE.getValue(), UserCredentialsDuplicateException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.User.CREDENTIALS_DUPLICATE.getValue());
	}
}
