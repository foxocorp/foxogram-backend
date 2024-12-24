package su.foxogram.exceptions.channel;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;
import su.foxogram.exceptions.BaseException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ChannelNotFoundException extends BaseException {

	public ChannelNotFoundException() {
		super(ExceptionsConstants.Messages.CHANNEL_NOT_FOUND.getValue(), ChannelNotFoundException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Channel.NOT_FOUND.getValue());
	}
}