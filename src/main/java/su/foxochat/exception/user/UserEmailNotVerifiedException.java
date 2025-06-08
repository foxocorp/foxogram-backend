package su.foxochat.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxochat.constant.ExceptionConstant;
import su.foxochat.exception.BaseException;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserEmailNotVerifiedException extends BaseException {

	public UserEmailNotVerifiedException() {
		super(ExceptionConstant.Messages.USER_EMAIL_VERIFIED.getValue(), UserEmailNotVerifiedException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.User.EMAIL_NOT_VERIFIED.getValue());
	}
}
