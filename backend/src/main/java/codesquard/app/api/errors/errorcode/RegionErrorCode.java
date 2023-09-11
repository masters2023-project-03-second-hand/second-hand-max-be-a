package codesquard.app.api.errors.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum RegionErrorCode implements ErrorCode {

	NOT_FOUND_REGION(HttpStatus.NOT_FOUND, "주소를 찾지 못하였습니다."),
	NOT_MATCH_ADDRESS(HttpStatus.BAD_REQUEST, "전체 주소와 동주소가 서로 일치하지 않습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	RegionErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

	@Override
	public String getName() {
		return name();
	}

	@Override
	public String toString() {
		return String.format("%s, %s(name=%s, httpStatus=%s, message=%s)", "지역(동네) 에러", this.getClass().getSimpleName(),
			name(),
			httpStatus,
			message);
	}
}
