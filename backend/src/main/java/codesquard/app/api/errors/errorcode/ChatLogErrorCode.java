package codesquard.app.api.errors.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ChatLogErrorCode implements ErrorCode {

	NOT_FOUND_CHAT_LOG(HttpStatus.NOT_FOUND, "채팅이 존재하지 않습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	ChatLogErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

	@Override
	public String getName() {
		return name();
	}

	@Override
	public String toString() {
		return String.format("%s, %s(name=%s, httpStatus=%s, message=%s)", "채팅 메시지 에러", this.getClass().getSimpleName(),
			name(),
			httpStatus,
			message);
	}
}
