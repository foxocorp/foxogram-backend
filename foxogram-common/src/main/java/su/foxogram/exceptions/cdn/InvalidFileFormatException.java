package su.foxogram.exceptions.cdn;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;
import su.foxogram.exceptions.BaseException;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class InvalidFileFormatException extends BaseException {

	public InvalidFileFormatException() {
		super(ExceptionsConstants.Messages.INVALID_FILE_FORMAT.getValue(), InvalidFileFormatException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.CDN.INVALID_FILE_FORMAT.getValue());
	}
}