package codesquard.app.api.errors.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum CategoryErrorCode implements ErrorCode {

	NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	CategoryErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

	@Override
	public String getName() {
		return name();
	}

	@Override
	public String toString() {
		return String.format("%s, %s(httpStatus=%s, message=%s)", "Oauth 에러", this.getClass().getSimpleName(),
			httpStatus,
			message);
	}
}
