package app.foxochat.exception.media;

import app.foxochat.constant.ExceptionConstant;
import app.foxochat.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MediaNotFoundException extends BaseException {

    public MediaNotFoundException() {
        super(
                ExceptionConstant.Messages.MEDIA_NOT_FOUND.getValue(),
                MediaNotFoundException.class.getAnnotation(ResponseStatus.class).value(),
                ExceptionConstant.Media.NOT_FOUND.getValue()
        );
    }
}
