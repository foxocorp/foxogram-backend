package app.foxochat.exception.member;

import app.foxochat.constant.ExceptionConstant;
import app.foxochat.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class MissingPermissionsException extends BaseException {

    public MissingPermissionsException() {
        super(
                ExceptionConstant.Messages.MISSING_PERMISSIONS.getValue(),
                MissingPermissionsException.class.getAnnotation(ResponseStatus.class).value(),
                ExceptionConstant.Member.MISSING_PERMISSIONS.getValue()
        );
    }
}
