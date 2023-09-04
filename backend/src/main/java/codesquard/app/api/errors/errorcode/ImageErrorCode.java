package codesquard.app.api.errors.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ImageErrorCode implements ErrorCode {

	INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 확장자입니다."),
	FILE_IO_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "파일 입출력에 실패했습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	ImageErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

	@Override
	public String getName() {
		return name();
	}

	@Override
	public String toString() {
		return String.format("%s, %s(httpStatus=%s, message=%s)", "이미지 에러", this.getClass().getSimpleName(), httpStatus,
			message);
	}
}
