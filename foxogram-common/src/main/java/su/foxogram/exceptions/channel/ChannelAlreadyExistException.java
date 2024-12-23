package su.foxogram.exceptions.channel;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;
import su.foxogram.exceptions.BaseException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ChannelAlreadyExistException extends BaseException {

	public ChannelAlreadyExistException() {
		super("Channel with this name already exist", ChannelAlreadyExistException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Channel.ALREADY_EXIST.getValue());
	}
}