package su.foxogram.exceptions.member;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;
import su.foxogram.exceptions.BaseException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MemberAlreadyInChannelException extends BaseException {

	public MemberAlreadyInChannelException() {
		super("You've already joined this channel.", MemberAlreadyInChannelException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Member.ALREADY_EXIST.getValue());
	}
}