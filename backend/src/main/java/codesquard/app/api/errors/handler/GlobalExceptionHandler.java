package codesquard.app.api.errors.handler;

import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import codesquard.app.api.errors.exception.SecondHandException;
import codesquard.app.api.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(SecondHandException.class)
	public ResponseEntity<ApiResponse<Object>> handleSecondHandException(SecondHandException exception) {
		log.error("SecondHandException 발생 : {}", exception.toString());
		ApiResponse<Object> body = ApiResponse.error(exception.getErrorCode());
		return ResponseEntity.status(exception.getErrorCode().getHttpStatus()).body(body);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException exception) {
		log.error("MethodArgumentNotValidException 발생 : {}", exception.toString());
		ApiResponse<Object> body = ApiResponse.of(
			HttpStatus.BAD_REQUEST,
			"유효하지 않은 입력형식입니다.",
			exception.getBindingResult().getFieldErrors().stream()
				.map(error -> {
					Map<String, String> errors = new HashMap<>();
					errors.put("field", error.getField());
					errors.put("defaultMessage", error.getDefaultMessage());
					return errors;
				}).distinct()
		);
		return ResponseEntity.badRequest().body(body);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(
		ConstraintViolationException exception) {
		log.error("ConstraintViolationException 발생 : {}", exception.toString());
		ApiResponse<Object> body = ApiResponse.of(
			HttpStatus.BAD_REQUEST,
			"유효하지 않은 입력형식입니다.",
			exception.getConstraintViolations().stream()
				.map(error -> {
					Map<String, String> errors = new HashMap<>();

					errors.put("field", error.getPropertyPath().toString());
					errors.put("defaultMessage", error.getMessage());
					return errors;
				}).distinct()
		);
		return ResponseEntity.badRequest().body(body);
	}

}
