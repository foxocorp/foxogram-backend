package su.foxogram.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import su.foxogram.configs.APIConfig;
import su.foxogram.constants.ExceptionsConstants;
import su.foxogram.dtos.response.ExceptionDTO;
import su.foxogram.exceptions.BaseException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

	private final APIConfig apiConfig;

	public ExceptionController(APIConfig apiConfig) {
		this.apiConfig = apiConfig;
	}

	private ResponseEntity<ExceptionDTO> buildErrorResponse(int errorCode, String message, HttpStatus status) {
		log.error(ExceptionsConstants.Messages.SERVER_EXCEPTION.getValue(), errorCode, status, message);
		return ResponseEntity.status(status).body(new ExceptionDTO(false, errorCode, message));
	}

	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ExceptionDTO> handleBaseException(BaseException exception) {
		return buildErrorResponse(exception.getErrorCode(), exception.getMessage(), exception.getStatus());
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ExceptionDTO> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
		return buildErrorResponse(ExceptionsConstants.API.EMPTY_BODY.getValue(), ExceptionsConstants.Messages.REQUEST_BODY_EMPTY.getValue(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionDTO> handleValidationException(MethodArgumentNotValidException exception) {
		String message = exception.getBindingResult().getAllErrors().stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage)
				.collect(Collectors.joining(", "));

		return buildErrorResponse(ExceptionsConstants.API.VALIDATION_ERROR.getValue(), message, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionDTO> handleException(Exception exception) {
		String message = exception.getMessage();
		if (!apiConfig.isDevelopment()) message = ExceptionsConstants.Messages.INTERNAL_ERROR.getValue();

		log.error(ExceptionsConstants.Messages.SERVER_EXCEPTION_STACKTRACE.getValue(), exception);
		return buildErrorResponse(ExceptionsConstants.Unknown.ERROR.getValue(), message, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
