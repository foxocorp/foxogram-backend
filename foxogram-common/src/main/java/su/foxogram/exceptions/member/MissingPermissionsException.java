package su.foxogram.exceptions.member;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;
import su.foxogram.exceptions.BaseException;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class MissingPermissionsException extends BaseException {

	public MissingPermissionsException() {
		super(ExceptionsConstants.Messages.MISSING_PERMISSIONS.getValue(), MissingPermissionsException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Member.MISSING_PERMISSIONS.getValue());
	}
}