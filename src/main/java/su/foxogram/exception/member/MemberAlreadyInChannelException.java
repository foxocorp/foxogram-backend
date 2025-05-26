package su.foxogram.exception.member;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constant.ExceptionConstant;
import su.foxogram.exception.BaseException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MemberAlreadyInChannelException extends BaseException {

	public MemberAlreadyInChannelException() {
		super(ExceptionConstant.Messages.MEMBER_ALREADY_EXIST.getValue(), MemberAlreadyInChannelException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.Member.ALREADY_EXIST.getValue());
	}
}
