package codesquard.app.api.errors.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum MemberTownErrorCode implements ErrorCode {

	MAXIMUM_MEMBER_TOWN_SIZE(HttpStatus.CONFLICT, "회원이 가질 수 있는 개수(최대2개)를 초과하였습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	MemberTownErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

	@Override
	public String getName() {
		return name();
	}

	@Override
	public String toString() {
		return String.format("%s, %s(name=%s, httpStatus=%s, message=%s)", "회원 에러", this.getClass().getSimpleName(),
			name(),
			httpStatus,
			message);
	}
}
