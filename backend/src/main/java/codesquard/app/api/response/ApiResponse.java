package codesquard.app.api.response;

import org.springframework.http.HttpStatus;

import codesquard.app.api.errors.errorcode.ErrorCode;
import lombok.Getter;

@Getter
public class ApiResponse<T> {

	private final int statusCode;
	private final String message;
	private final T data;

	public ApiResponse(HttpStatus status, String message, T data) {
		this.statusCode = status.value();
		this.message = message;
		this.data = data;
	}

	public static <T> ApiResponse<T> of(HttpStatus httpStatus, String message, T data) {
		return new ApiResponse<>(httpStatus, message, data);
	}

	public static <T> ApiResponse<T> ok(String message, T data) {
		return new ApiResponse<>(HttpStatus.OK, message, data);
	}

	public static <T> ApiResponse<T> created(String message, T data) {
		return new ApiResponse<>(HttpStatus.CREATED, message, data);
	}

	public static <T> ApiResponse<T> error(ErrorCode errorCode) {
		return new ApiResponse<>(errorCode.getHttpStatus(), errorCode.getMessage(), null);
	}

	@Override
	public String toString() {
		return String.format("%s, %s(statusCode=%d, message=%s)", "API 공통 응답", this.getClass().getSimpleName(),
			statusCode,
			message);
	}
}
