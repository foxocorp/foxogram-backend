package app.foxochat.exception.user;

import app.foxochat.constant.ExceptionConstant;
import app.foxochat.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserContactNotFoundException extends BaseException {

    public UserContactNotFoundException() {
        super(
                ExceptionConstant.Messages.USER_CONTACT_NOT_FOUND.getValue(),
                UserContactNotFoundException.class.getAnnotation(ResponseStatus.class).value(),
                ExceptionConstant.User.CONTACT_NOT_FOUND.getValue()
        );
    }
}
