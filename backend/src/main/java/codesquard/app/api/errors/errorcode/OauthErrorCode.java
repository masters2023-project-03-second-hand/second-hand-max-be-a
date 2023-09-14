package codesquard.app.api.errors.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum OauthErrorCode implements ErrorCode {
	WRONG_AUTHORIZATION_CODE(HttpStatus.BAD_REQUEST, "잘못된 인가 코드입니다."),
	ALREADY_LOGOUT(HttpStatus.UNAUTHORIZED, "이미 로그아웃 상태입니다."),
	NOT_LOGIN_STATE(HttpStatus.UNAUTHORIZED, "로그인 상태가 아닙니다."),
	NOT_FOUND_PROVIDER(HttpStatus.NOT_FOUND, "provider를 찾을 수 없습니다."),
	FAIL_LOGIN(HttpStatus.UNAUTHORIZED, "로그인 정보가 일치하지 않습니다."),
	ALREADY_SIGNUP(HttpStatus.UNAUTHORIZED, "이미 회원가입된 상태입니다.");

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

	@Override
	public String toString() {
		return String.format("%s, %s(name=%s, httpStatus=%s, message=%s)", "Oauth 에러", this.getClass().getSimpleName(),
			name(),
			httpStatus,
			message);
	}
}
