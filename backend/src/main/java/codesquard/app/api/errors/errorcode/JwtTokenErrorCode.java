package codesquard.app.api.errors.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum JwtTokenErrorCode implements ErrorCode {

	EXPIRE_TOKEN(HttpStatus.FORBIDDEN, "토큰이 만료되었습니다."),
	INVALID_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다."),
	EMPTY_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 존재하지 않습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	JwtTokenErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

	@Override
	public String getName() {
		return name();
	}

	@Override
	public String toString() {
		return String.format("%s, %s(name=%s, httpStatus=%s, message=%s)", "JWT 토큰 에러", this.getClass().getSimpleName(),
			name(),
			httpStatus,
			message);
	}
}
