package codesquard.app.api.errors.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum WishErrorCode implements ErrorCode {

	INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");

	private final HttpStatus httpStatus;
	private final String message;

	WishErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

	@Override
	public String getName() {
		return name();
	}

	@Override
	public String toString() {
		return String.format("%s, %s(name=%s, httpStatus=%s, message=%s)", "관심상품 에러", this.getClass().getSimpleName(),
			name(),
			httpStatus,
			message);
	}
}
