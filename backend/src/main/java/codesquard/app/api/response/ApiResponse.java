package codesquard.app.api.response;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import codesquard.app.api.errors.errorcode.ErrorCode;
import lombok.Getter;

@Getter
public class ApiResponse<T> {
	private final int statusCode;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final String message;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final T data;

	public ApiResponse(HttpStatus status, String message, T data) {
		this.statusCode = status.value();
		this.message = message;
		this.data = data;
	}

	public static <T> ApiResponse<T> of(HttpStatus httpStatus, String message, T data) {
		return new ApiResponse<>(httpStatus, message, data);
	}

	public static <T> ApiResponse<T> ok() {
		return new ApiResponse<>(HttpStatus.OK, null, null);
	}

	public static <T> ApiResponse<T> created() {
		return new ApiResponse<>(HttpStatus.CREATED, null, null);
	}

	public static <T> ApiResponse<T> error(ErrorCode errorCode) {
		return new ApiResponse<>(errorCode.getHttpStatus(), errorCode.getMessage(), null);
	}
}
