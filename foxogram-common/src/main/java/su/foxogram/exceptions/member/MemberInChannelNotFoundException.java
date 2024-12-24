package su.foxogram.exceptions.member;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;
import su.foxogram.exceptions.BaseException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MemberInChannelNotFoundException extends BaseException {

	public MemberInChannelNotFoundException() {
		super(ExceptionsConstants.Messages.MEMBER_NOT_FOUND.getValue(), MemberInChannelNotFoundException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Member.NOT_FOUND.getValue());
	}
}