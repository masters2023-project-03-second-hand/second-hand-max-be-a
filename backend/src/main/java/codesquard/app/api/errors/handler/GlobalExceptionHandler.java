package codesquard.app.api.errors.handler;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(RestApiException.class)
	public ResponseEntity<ApiResponse<Object>> handleRestApiException(RestApiException exception) {
		log.error("RestApiException 발생 : {}", exception.toString());
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
				})
		);
		return ResponseEntity.badRequest().body(body);
	}

}
