package codesquard.app.api.errors.exception;

import codesquard.app.api.errors.errorcode.ErrorCode;

public class UnAuthorizationException extends SecondHandException {

	public UnAuthorizationException(ErrorCode errorCode) {
		super(errorCode);
	}

	public UnAuthorizationException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}
}
