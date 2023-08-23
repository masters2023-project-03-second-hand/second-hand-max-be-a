package codesquard.app.api.errors.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(RestApiException.class)
	public ResponseEntity<ApiResponse<Object>> handleUserRestApiException(RestApiException e) {
		log.error("RestApiException 발생 : {}", e.toString());
		ApiResponse<Object> body = ApiResponse.error(e.getErrorCode());
		return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(body);
	}
}
