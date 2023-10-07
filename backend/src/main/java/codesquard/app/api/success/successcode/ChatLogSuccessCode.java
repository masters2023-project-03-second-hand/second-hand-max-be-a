package codesquard.app.api.success.successcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatLogSuccessCode implements SuccessCode {

	CREATED_CHAT_LOG(HttpStatus.CREATED, "메시지 전송이 완료되었습니다."),
	OK_NOT_NEW_CHAT_LOG(HttpStatus.OK, "새로운 채팅 메시지가 존재하지 않습니다."),
	OK_CHAT_LOGS(HttpStatus.OK, "채팅 메시지 목록 조회가 완료되었습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	@Override
	public String toString() {
		return String.format("%s, %s(name=%s, httpStatus=%s, message=%s)", "채팅 메시지 성공 코드",
			this.getClass().getSimpleName(),
			name(),
			httpStatus,
			message);
	}
}
