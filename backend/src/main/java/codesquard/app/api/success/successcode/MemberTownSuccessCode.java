package codesquard.app.api.success.successcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberTownSuccessCode implements SuccessCode {
	CREATED_MEMBER_TOWN(HttpStatus.CREATED, "동네 추가에 성공하였습니다."),
	OK_DELETED_MEMBER_TOWN(HttpStatus.OK, "동네 삭제에 성공하였습니다."),
	OK_SELECTED_MEMBER_TOWN(HttpStatus.OK, "지역 선택을 완료하였습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	@Override
	public String toString() {
		return String.format("%s, %s(name=%s, httpStatus=%s, message=%s)", "회원 동네 성공 코드",
			this.getClass().getSimpleName(),
			name(),
			httpStatus,
			message);
	}
}
