package su.foxogram.exceptions.code;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;
import su.foxogram.exceptions.BaseException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CodeExpiredException extends BaseException {

	public CodeExpiredException() {
		super("Code has expired!", CodeExpiredException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Code.EXPIRED.getValue());
	}
}