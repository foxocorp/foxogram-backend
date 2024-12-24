package su.foxogram.exceptions.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;
import su.foxogram.exceptions.BaseException;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserEmailNotVerifiedException extends BaseException {

	public UserEmailNotVerifiedException() {
		super(ExceptionsConstants.Messages.USER_EMAIL_VERIFIED.getValue(), UserEmailNotVerifiedException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.User.EMAIL_NOT_VERIFIED.getValue());
	}
}