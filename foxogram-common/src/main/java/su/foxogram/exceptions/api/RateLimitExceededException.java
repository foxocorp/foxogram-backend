package su.foxogram.exceptions.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;
import su.foxogram.exceptions.BaseException;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class RateLimitExceededException extends BaseException {

	public RateLimitExceededException(long estimateAbilityToConsumeInMs) {
		super("Rate-limit exceeded! " + estimateAbilityToConsumeInMs / 1000 + "s", RateLimitExceededException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.API.RATE_LIMIT_EXCEEDED.getValue());
	}
}