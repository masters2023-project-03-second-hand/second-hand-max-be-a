package codesquard.app.api.success.successcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatRoomSuccessCode implements SuccessCode {
	CREATED_CHAT_ROOM(HttpStatus.CREATED, "채팅방 생성을 완료하였습니다."),
	OK_CHAT_ROOMS(HttpStatus.OK, "채팅방 목록 조회를 완료하였습니다."),
	OK_CHAT_ROOMS_BY_ITEMS(HttpStatus.OK, "상품에 따른 채팅방 목록 조회를 완료하였습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	@Override
	public String toString() {
		return String.format("%s, %s(name=%s, httpStatus=%s, message=%s)", "채팅방 성공 코드",
			this.getClass().getSimpleName(),
			name(),
			httpStatus,
			message);
	}
}
