package su.foxochat.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxochat.constant.ExceptionConstant;
import su.foxochat.exception.BaseException;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserUnauthorizedException extends BaseException {

	public UserUnauthorizedException() {
		super(ExceptionConstant.Messages.USER_UNAUTHORIZED.getValue(), UserUnauthorizedException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.User.UNAUTHORIZED.getValue());
	}
}
