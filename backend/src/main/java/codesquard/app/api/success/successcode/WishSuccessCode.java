package codesquard.app.api.success.successcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WishSuccessCode implements SuccessCode {
	OK_MODIFIED_WISH_STATUS(HttpStatus.OK, "관심상품 변경이 완료되었습니다."),
	OK_WISHES(HttpStatus.OK, "관심상품 조회에 성공하였습니다."),
	OK_WISH_CATEGORIES(HttpStatus.OK, "관심상품의 카테고리 목록 조회를 완료하였습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	@Override
	public String toString() {
		return String.format("%s, %s(name=%s, httpStatus=%s, message=%s)", "관심상품 성공 코드",
			this.getClass().getSimpleName(),
			name(),
			httpStatus,
			message);
	}
}
