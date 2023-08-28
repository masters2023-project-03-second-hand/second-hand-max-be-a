package codesquard.app.api.errors.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum OauthErrorCode implements ErrorCode {
	WRONG_AUTHORIZATION_CODE(HttpStatus.BAD_REQUEST, "잘못된 인가 코드입니다."),
	NOT_FOUND_PROVIDER(HttpStatus.NOT_FOUND, "provider를 찾을 수 없습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	OauthErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

	@Override
	public String getName() {
		return name();
	}
}
