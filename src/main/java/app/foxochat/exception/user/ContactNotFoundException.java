package app.foxochat.exception.user;

import app.foxochat.constant.ExceptionConstant;
import app.foxochat.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ContactNotFoundException extends BaseException {

    public ContactNotFoundException() {
        super(
                ExceptionConstant.Messages.CONTACT_NOT_FOUND.getValue(),
                ContactNotFoundException.class.getAnnotation(ResponseStatus.class).value(),
                ExceptionConstant.User.CONTACT_NOT_FOUND.getValue()
        );
    }
}
