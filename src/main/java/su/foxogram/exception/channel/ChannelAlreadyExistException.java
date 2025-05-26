package su.foxogram.exception.channel;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constant.ExceptionConstant;
import su.foxogram.exception.BaseException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ChannelAlreadyExistException extends BaseException {

	public ChannelAlreadyExistException() {
		super(ExceptionConstant.Messages.CHANNEL_ALREADY_EXIST.getValue(), ChannelAlreadyExistException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.Channel.ALREADY_EXIST.getValue());
	}
}
