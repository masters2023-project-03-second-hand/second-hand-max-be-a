package codesquard.app.api.errors.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemErrorCode implements ErrorCode {

	INVALID_STATUS(HttpStatus.BAD_REQUEST, "상태는 판매중, 예약중, 판매완료만 들어올 수 있습니다."),
	ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
	ITEM_FORBIDDEN(HttpStatus.FORBIDDEN, "상품에 대한 권한이 없습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	@Override
	public String toString() {
		return String.format("%s, %s(name=%s, httpStatus=%s, message=%s)", "상품 에러 코드",
			this.getClass().getSimpleName(),
			name(),
			httpStatus,
			message);
	}
}
