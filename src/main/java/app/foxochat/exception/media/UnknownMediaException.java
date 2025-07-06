package app.foxochat.exception.media;

import app.foxochat.constant.ExceptionConstant;
import app.foxochat.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnknownMediaException extends BaseException {

    public UnknownMediaException() {
        super(
                ExceptionConstant.Messages.UNKNOWN_MEDIA.getValue(),
                UnknownMediaException.class.getAnnotation(ResponseStatus.class).value(),
                ExceptionConstant.Media.UNKNOWN.getValue()
        );
    }
}
