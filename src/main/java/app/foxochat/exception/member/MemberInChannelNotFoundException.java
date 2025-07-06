package app.foxochat.exception.member;

import app.foxochat.constant.ExceptionConstant;
import app.foxochat.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MemberInChannelNotFoundException extends BaseException {

    public MemberInChannelNotFoundException() {
        super(
                ExceptionConstant.Messages.MEMBER_NOT_FOUND.getValue(),
                MemberInChannelNotFoundException.class.getAnnotation(ResponseStatus.class).value(),
                ExceptionConstant.Member.NOT_FOUND.getValue()
        );
    }
}
