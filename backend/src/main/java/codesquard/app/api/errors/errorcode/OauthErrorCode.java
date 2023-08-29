package codesquard.app.api.errors.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum OauthErrorCode implements ErrorCode {
	WRONG_AUTHORIZATION_CODE(HttpStatus.UNAUTHORIZED, "잘못된 인가 코드입니다."),
	ALREADY_LOGOUT(HttpStatus.UNAUTHORIZED, "이미 로그아웃 상태입니다."),
	NOT_FOUND_PROVIDER(HttpStatus.NOT_FOUND, "provider를 찾을 수 없습니다."),
	FAIL_LOGIN(HttpStatus.BAD_REQUEST, "로그인 정보가 일치하지 않습니다.");

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
