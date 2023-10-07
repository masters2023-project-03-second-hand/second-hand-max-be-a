package codesquard.app.api.errors.exception;

import codesquard.app.api.errors.errorcode.ErrorCode;

public class OauthException extends SecondHandException {

	public OauthException(ErrorCode errorCode) {
		super(errorCode);
	}

	public OauthException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}
}
