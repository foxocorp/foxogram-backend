package app.foxochat.exception.user;

import app.foxochat.constant.ExceptionConstant;
import app.foxochat.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserEmailNotVerifiedException extends BaseException {

	public UserEmailNotVerifiedException() {
		super(ExceptionConstant.Messages.USER_EMAIL_VERIFIED.getValue(), UserEmailNotVerifiedException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.User.EMAIL_NOT_VERIFIED.getValue());
	}
}
