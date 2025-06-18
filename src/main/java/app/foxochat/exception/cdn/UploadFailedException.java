package app.foxochat.exception.cdn;

import app.foxochat.constant.ExceptionConstant;
import app.foxochat.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class UploadFailedException extends BaseException {

	public UploadFailedException() {
		super(ExceptionConstant.Messages.UPLOAD_FAILED.getValue(), UploadFailedException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.CDN.UPLOAD_FAILED.getValue());
	}
}
