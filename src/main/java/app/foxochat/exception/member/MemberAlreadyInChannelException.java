package app.foxochat.exception.member;

import app.foxochat.constant.ExceptionConstant;
import app.foxochat.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MemberAlreadyInChannelException extends BaseException {

    public MemberAlreadyInChannelException() {
        super(
                ExceptionConstant.Messages.MEMBER_ALREADY_EXIST.getValue(),
                MemberAlreadyInChannelException.class.getAnnotation(ResponseStatus.class).value(),
                ExceptionConstant.Member.ALREADY_EXIST.getValue()
        );
    }
}
