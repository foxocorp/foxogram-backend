package su.foxogram.exception.member;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constant.ExceptionConstant;
import su.foxogram.exception.BaseException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MemberInChannelNotFoundException extends BaseException {

	public MemberInChannelNotFoundException() {
		super(ExceptionConstant.Messages.MEMBER_NOT_FOUND.getValue(), MemberInChannelNotFoundException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.Member.NOT_FOUND.getValue());
	}
}
