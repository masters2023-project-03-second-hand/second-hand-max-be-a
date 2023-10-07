package codesquard.app.api.errors.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatLogErrorCode implements ErrorCode {

	NOT_FOUND_CHAT_LOG(HttpStatus.NOT_FOUND, "채팅이 존재하지 않습니다."),
	FORBIDDEN_CHAT_LOG(HttpStatus.FORBIDDEN, "채팅에 대한 권한이 없습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	@Override
	public String toString() {
		return String.format("%s, %s(name=%s, httpStatus=%s, message=%s)", "채팅 메시지 에러 코드",
			this.getClass().getSimpleName(),
			name(),
			httpStatus,
			message);
	}
}
