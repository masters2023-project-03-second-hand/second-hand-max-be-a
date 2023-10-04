package codesquard.app.api.errors.exception;

import codesquard.app.api.errors.errorcode.ErrorCode;

public class ServerInternalException extends SecondHandException {

	public ServerInternalException(ErrorCode errorCode) {
		super(errorCode);
	}

	public ServerInternalException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}
}
