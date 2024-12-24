package su.foxogram.exceptions.channel;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;
import su.foxogram.exceptions.BaseException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ChannelAlreadyExistException extends BaseException {

	public ChannelAlreadyExistException() {
		super(ExceptionsConstants.Messages.CHANNEL_ALREADY_EXIST.getValue(), ChannelAlreadyExistException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Channel.ALREADY_EXIST.getValue());
	}
}