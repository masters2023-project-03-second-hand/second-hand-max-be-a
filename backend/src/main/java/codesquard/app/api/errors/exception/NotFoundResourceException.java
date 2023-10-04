package codesquard.app.api.errors.exception;

import codesquard.app.api.errors.errorcode.ErrorCode;

public class NotFoundResourceException extends SecondHandException {

	public NotFoundResourceException(ErrorCode errorCode) {
		super(errorCode);
	}

	public NotFoundResourceException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}
}
