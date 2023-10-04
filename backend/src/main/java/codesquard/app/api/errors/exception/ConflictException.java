package codesquard.app.api.errors.exception;

import codesquard.app.api.errors.errorcode.ErrorCode;

public class ConflictException extends SecondHandException {

	public ConflictException(ErrorCode errorCode) {
		super(errorCode);
	}

	public ConflictException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}
}
