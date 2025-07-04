package app.foxochat.exception.media;

import app.foxochat.constant.ExceptionConstant;
import app.foxochat.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MediaCannotBeEmptyException extends BaseException {

	public MediaCannotBeEmptyException() {
		super(ExceptionConstant.Messages.MEDIA_CANNOT_BE_EMPTY.getValue(), MediaCannotBeEmptyException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.Media.CANNOT_BE_EMPTY.getValue());
	}
}
