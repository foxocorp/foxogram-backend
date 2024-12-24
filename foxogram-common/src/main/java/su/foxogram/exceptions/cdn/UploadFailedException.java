package su.foxogram.exceptions.cdn;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;
import su.foxogram.exceptions.BaseException;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class UploadFailedException extends BaseException {

	public UploadFailedException() {
		super(ExceptionsConstants.Messages.UPLOAD_FAILED.getValue(), UploadFailedException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.CDN.UPLOAD_FAILED.getValue());
	}
}