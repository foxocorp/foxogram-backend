package su.foxogram.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import su.foxogram.config.APIConfig;
import su.foxogram.constant.ExceptionConstant;
import su.foxogram.dto.api.response.ExceptionDTO;
import su.foxogram.exception.BaseException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

	private final APIConfig apiConfig;

	public ExceptionController(APIConfig apiConfig) {
		this.apiConfig = apiConfig;
	}

	private ResponseEntity<ExceptionDTO> buildErrorResponse(int errorCode, String message, HttpStatus status) {
		log.error(ExceptionConstant.Messages.SERVER_EXCEPTION.getValue(), errorCode, status, message);
		return ResponseEntity.status(status).body(new ExceptionDTO(false, errorCode, message));
	}

	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ExceptionDTO> handleBaseException(BaseException exception) {
		return buildErrorResponse(exception.getErrorCode(), exception.getMessage(), exception.getStatus());
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ExceptionDTO> handleHttpMessageNotReadable() {
		return buildErrorResponse(ExceptionConstant.API.EMPTY_BODY.getValue(), ExceptionConstant.Messages.REQUEST_BODY_EMPTY.getValue(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionDTO> handleValidationException(MethodArgumentNotValidException exception) {
		String message = exception.getBindingResult().getAllErrors()
				.stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage)
				.collect(Collectors.joining(", "));

		return buildErrorResponse(ExceptionConstant.API.VALIDATION_ERROR.getValue(), message, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ExceptionDTO> handleConstraintViolationException(ConstraintViolationException exception) {
		String message = exception.getConstraintViolations()
				.stream()
				.map(ConstraintViolation::getMessage)
				.collect(Collectors.joining(", "));

		return buildErrorResponse(ExceptionConstant.API.VALIDATION_ERROR.getValue(), message, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ExceptionDTO> handleNoHandlerFoundException() {
		return buildErrorResponse(ExceptionConstant.API.ROUTE_NOT_FOUND.getValue(), ExceptionConstant.Messages.ROUTE_NOT_FOUND.getValue(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionDTO> handleException(Exception exception) {
		String message = exception.getMessage();
		if (!apiConfig.isDevelopment()) message = ExceptionConstant.Messages.INTERNAL_ERROR.getValue();

		log.error(ExceptionConstant.Messages.SERVER_EXCEPTION_STACKTRACE.getValue(), exception);
		return buildErrorResponse(ExceptionConstant.Unknown.ERROR.getValue(), message, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
