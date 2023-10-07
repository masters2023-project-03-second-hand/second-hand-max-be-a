package codesquard.app.api.errors.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageErrorCode implements ErrorCode {
	INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 확장자입니다."),
	NOT_REMOVE_IMAGES(HttpStatus.BAD_REQUEST, "이미지는 최소 1개 이상 존재해야 합니다."),
	NOT_FOUND_IMAGE_URL(HttpStatus.NOT_FOUND, "해당 이미지 URL이 존재하지 않습니다."),
	FILE_IO_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "파일 입출력에 실패했습니다."),
	EMPTY_IMAGE(HttpStatus.INTERNAL_SERVER_ERROR, "상품에 대한 이미지가 없습니다."),
	FAIL_REMOVE_IMAGE(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 삭제가 실패하였습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	@Override
	public String toString() {
		return String.format("%s, %s(name=%s, httpStatus=%s, message=%s)", "이미지 에러 코드",
			this.getClass().getSimpleName(),
			name(),
			httpStatus,
			message);
	}
}
