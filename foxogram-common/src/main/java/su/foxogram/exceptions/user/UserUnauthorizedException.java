package su.foxogram.exceptions.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;
import su.foxogram.exceptions.BaseException;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserUnauthorizedException extends BaseException {

	public UserUnauthorizedException() {
		super(ExceptionsConstants.Messages.USER_UNAUTHORIZED.getValue(), UserUnauthorizedException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.User.UNAUTHORIZED.getValue());
	}
}