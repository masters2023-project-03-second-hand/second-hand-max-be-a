package codesquard.app.api.errors.exception;

import codesquard.app.api.errors.errorcode.ErrorCode;

public class ForBiddenException extends SecondHandException {

	public ForBiddenException(ErrorCode errorCode) {
		super(errorCode);
	}

	public ForBiddenException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}
}
