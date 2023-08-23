package codesquard.app.api.errors.errorcode;

import org.springframework.http.HttpStatus;

import lombok.ToString;

@ToString
public enum MemberErrorCode implements ErrorCode {

	ALREADY_EXIST_ID(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다.");

	private final HttpStatus httpStatus;
	private final String message;

	MemberErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

	@Override
	public String getName() {
		return name();
	}

	@Override
	public HttpStatus getHttpStatus() {
		return null;
	}

	@Override
	public String getMessage() {
		return null;
	}
}
