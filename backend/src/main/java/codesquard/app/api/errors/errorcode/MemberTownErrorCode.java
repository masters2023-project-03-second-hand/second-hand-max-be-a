package codesquard.app.api.errors.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberTownErrorCode implements ErrorCode {

	MAXIMUM_MEMBER_TOWN_SIZE(HttpStatus.BAD_REQUEST, "회원이 가질 수 있는 개수(최대2개)를 초과하였습니다."),
	MINIMUM_MEMBER_TOWN_SIZE(HttpStatus.BAD_REQUEST, "동네는 최소 1개 이상 선택해야 해요. 새로운 동네를 등록한 후 삭제해주세요."),
	UNREGISTERED_ADDRESS_TO_REMOVE(HttpStatus.BAD_REQUEST, "등록되지 않은 동네를 삭제할 수 없습니다."),
	NOT_SELECT_UNREGISTERED_MEMBER_TOWN(HttpStatus.BAD_REQUEST, "회원이 등록한 동네만 선택할 수 있습니다."),
	NOT_FOUND_MEMBER_TOWN(HttpStatus.NOT_FOUND, "회원 동네를 찾을 수 없습니다."),
	ALREADY_ADDRESS_NAME(HttpStatus.CONFLICT, "이미 존재하는 동네입니다."),
	FAIL_REMOVE_ADDRESS(HttpStatus.INTERNAL_SERVER_ERROR, "동네 삭제를 실패하였습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	@Override
	public String toString() {
		return String.format("%s, %s(name=%s, httpStatus=%s, message=%s)", "회원 동네 에러 코드",
			this.getClass().getSimpleName(),
			name(),
			httpStatus,
			message);
	}
}
