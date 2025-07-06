package app.foxochat.exception.user;

import app.foxochat.constant.ExceptionConstant;
import app.foxochat.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserUnauthorizedException extends BaseException {

    public UserUnauthorizedException() {
        super(
                ExceptionConstant.Messages.USER_UNAUTHORIZED.getValue(),
                UserUnauthorizedException.class.getAnnotation(ResponseStatus.class).value(),
                ExceptionConstant.User.UNAUTHORIZED.getValue()
        );
    }
}
