package app.foxochat.exception.channel;

import app.foxochat.constant.ExceptionConstant;
import app.foxochat.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ChannelNotFoundException extends BaseException {

	public ChannelNotFoundException() {
		super(ExceptionConstant.Messages.CHANNEL_NOT_FOUND.getValue(), ChannelNotFoundException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.Channel.NOT_FOUND.getValue());
	}
}
