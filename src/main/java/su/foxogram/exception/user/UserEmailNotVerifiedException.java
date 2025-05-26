package su.foxogram.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constant.ExceptionConstant;
import su.foxogram.exception.BaseException;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserEmailNotVerifiedException extends BaseException {

	public UserEmailNotVerifiedException() {
		super(ExceptionConstant.Messages.USER_EMAIL_VERIFIED.getValue(), UserEmailNotVerifiedException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.User.EMAIL_NOT_VERIFIED.getValue());
	}
}
