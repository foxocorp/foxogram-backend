package su.foxogram.exception.cdn;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constant.ExceptionConstant;
import su.foxogram.exception.BaseException;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class UploadFailedException extends BaseException {

	public UploadFailedException() {
		super(ExceptionConstant.Messages.UPLOAD_FAILED.getValue(), UploadFailedException.class.getAnnotation(ResponseStatus.class).value(), ExceptionConstant.CDN.UPLOAD_FAILED.getValue());
	}
}
