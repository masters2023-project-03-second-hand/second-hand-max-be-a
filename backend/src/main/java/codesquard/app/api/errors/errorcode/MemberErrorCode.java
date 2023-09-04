package codesquard.app.api.errors.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum MemberErrorCode implements ErrorCode {

	ALREADY_EXIST_ID(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다."),
	NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "회원을 찾지 못하였습니다.");

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
	public String toString() {
		return String.format("%s, %s(httpStatus=%s, message=%s)", "회원 에러", this.getClass().getSimpleName(),
			httpStatus,
			message);
	}
}
