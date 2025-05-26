package su.foxogram.exception.channel;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constant.ExceptionConstant;
import su.foxogram.exception.BaseException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ChannelNotFoundException extends BaseException {

	public ChannelNotFoundException() {
		super(ExceptionConstant.Messages.CHANNEL_NOT_FOUND.getValue(), ChannelNotFoundException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.Channel.NOT_FOUND.getValue());
	}
}
