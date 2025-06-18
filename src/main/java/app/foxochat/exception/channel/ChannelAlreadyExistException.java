package app.foxochat.exception.channel;

import app.foxochat.constant.ExceptionConstant;
import app.foxochat.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ChannelAlreadyExistException extends BaseException {

	public ChannelAlreadyExistException() {
		super(ExceptionConstant.Messages.CHANNEL_ALREADY_EXIST.getValue(), ChannelAlreadyExistException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.Channel.ALREADY_EXIST.getValue());
	}
}
