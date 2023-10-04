package codesquard.app.api.errors.exception;

import codesquard.app.api.errors.errorcode.ErrorCode;
import lombok.Getter;

@Getter
public class SecondHandException extends RuntimeException {
	private final ErrorCode errorCode;
	private final String message;

	public SecondHandException(ErrorCode errorCode) {
		this(errorCode, errorCode.getMessage());
	}

	public SecondHandException(ErrorCode errorCode, String message) {
		this.errorCode = errorCode;
		this.message = message;
	}

	@Override
	public String toString() {
		return String.format("%s, %s(errorCode=%s, message=%s)", "중고거래 예외", this.getClass().getSimpleName(), errorCode,
			message);
	}
}
