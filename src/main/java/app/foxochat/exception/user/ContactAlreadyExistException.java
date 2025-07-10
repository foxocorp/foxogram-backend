package app.foxochat.exception.user;

import app.foxochat.constant.ExceptionConstant;
import app.foxochat.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ContactAlreadyExistException extends BaseException {

    public ContactAlreadyExistException() {
        super(
                ExceptionConstant.Messages.CONTACT_ALREADY_EXIST.getValue(),
                ContactAlreadyExistException.class.getAnnotation(ResponseStatus.class).value(),
                ExceptionConstant.User.CONTACT_ALREADY_EXIST.getValue()
        );
    }
}
