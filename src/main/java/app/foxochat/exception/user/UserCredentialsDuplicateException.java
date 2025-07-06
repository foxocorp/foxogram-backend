package app.foxochat.exception.user;

import app.foxochat.constant.ExceptionConstant;
import app.foxochat.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserCredentialsDuplicateException extends BaseException {

    public UserCredentialsDuplicateException() {
        super(
                ExceptionConstant.Messages.USER_CREDENTIALS_DUPLICATE.getValue(),
                UserCredentialsDuplicateException.class.getAnnotation(ResponseStatus.class).value(),
                ExceptionConstant.User.CREDENTIALS_DUPLICATE.getValue()
        );
    }
}
