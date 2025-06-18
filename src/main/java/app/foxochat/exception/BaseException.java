package app.foxochat.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
public abstract class BaseException extends Exception {

	@Getter
	public int errorCode;

	@Getter
	public HttpStatus status;

	public String message;

	public BaseException(String message, HttpStatus status, int errorCode) {
		this.message = message;
		this.status = status;
		this.errorCode = errorCode;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
