package app.foxochat.exception.media;

import app.foxochat.constant.ExceptionConstant;
import app.foxochat.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class InvalidFileFormatException extends BaseException {

	public InvalidFileFormatException() {
		super(ExceptionConstant.Messages.INVALID_FILE_FORMAT.getValue(), InvalidFileFormatException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.Media.INVALID_FILE_FORMAT.getValue());
	}
}
