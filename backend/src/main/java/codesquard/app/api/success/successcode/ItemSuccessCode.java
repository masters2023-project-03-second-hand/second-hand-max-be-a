package codesquard.app.api.success.successcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemSuccessCode implements SuccessCode {
	CREATED_ITEM(HttpStatus.CREATED, "상품 등록이 완료되었습니다."),
	OK_ITEMS(HttpStatus.OK, "상품 목록 조회에 성공하였습니다."),
	OK_DETAILED_ITEM(HttpStatus.OK, "상품 상세 조회에 성공하였습니다."),
	OK_MODIFIED_ITEM(HttpStatus.OK, "상품 수정을 완료하였습니다."),
	OK_MODIFIED_STATUS_ITEM(HttpStatus.OK, "상품 상태 변경에 성공하였습니다."),
	OK_DELETED_ITEM(HttpStatus.OK, "상품 삭제가 완료되었습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	@Override
	public String toString() {
		return String.format("%s, %s(name=%s, httpStatus=%s, message=%s)", "상품 성공 코드",
			this.getClass().getSimpleName(),
			name(),
			httpStatus,
			message);
	}
}
