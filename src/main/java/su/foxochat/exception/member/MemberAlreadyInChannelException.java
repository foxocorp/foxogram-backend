package su.foxochat.exception.member;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxochat.constant.ExceptionConstant;
import su.foxochat.exception.BaseException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MemberAlreadyInChannelException extends BaseException {

	public MemberAlreadyInChannelException() {
		super(ExceptionConstant.Messages.MEMBER_ALREADY_EXIST.getValue(), MemberAlreadyInChannelException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.Member.ALREADY_EXIST.getValue());
	}
}
