package codesquard.app.api.errors.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ItemErrorCode implements ErrorCode {

	INVALID_STATUS(HttpStatus.BAD_REQUEST, "상태는 판매중, 예약중, 판매완료만 들어올 수 있습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	ItemErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

	@Override
	public String getName() {
		return name();
	}
}
