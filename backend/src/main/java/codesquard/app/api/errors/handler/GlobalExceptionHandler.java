package codesquard.app.api.errors.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

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
				}).distinct()
		);
		return ResponseEntity.badRequest().body(body);
	}

}
