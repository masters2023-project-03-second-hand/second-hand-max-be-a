package codesquard.app.api.errors.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	// Category
	NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."),

	// Image
	INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 확장자입니다."),
	NOT_REMOVE_IMAGES(HttpStatus.BAD_REQUEST, "이미지는 최소 1개 이상 존재해야 합니다."),
	NOT_FOUND_IMAGE_URL(HttpStatus.NOT_FOUND, "해당 이미지 URL이 존재하지 않습니다."),
	FILE_IO_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "파일 입출력에 실패했습니다."),
	EMPTY_IMAGE(HttpStatus.INTERNAL_SERVER_ERROR, "상품에 대한 이미지가 없습니다."),
	FAIL_REMOVE_IMAGE(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 삭제가 실패하였습니다."),

	// Item
	INVALID_STATUS(HttpStatus.BAD_REQUEST, "상태는 판매중, 예약중, 판매완료만 들어올 수 있습니다."),
	ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
	ITEM_FORBIDDEN(HttpStatus.FORBIDDEN, "상품에 대한 권한이 없습니다."),

	// JWT
	INVALID_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다."),
	EMPTY_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 존재하지 않습니다."),
	EXPIRE_TOKEN(HttpStatus.FORBIDDEN, "토큰이 만료되었습니다."),

	// Member
	NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "회원을 찾지 못하였습니다."),
	ALREADY_EXIST_ID(HttpStatus.CONFLICT, "중복된 아이디입니다."),

	// MemberTown
	MAXIMUM_MEMBER_TOWN_SIZE(HttpStatus.BAD_REQUEST, "회원이 가질 수 있는 개수(최대2개)를 초과하였습니다."),
	MINIMUM_MEMBER_TOWN_SIZE(HttpStatus.BAD_REQUEST, "동네는 최소 1개 이상 선택해야 해요. 새로운 동네를 등록한 후 삭제해주세요."),
	UNREGISTERED_ADDRESS_TO_REMOVE(HttpStatus.BAD_REQUEST, "등록되지 않은 동네를 삭제할 수 없습니다."),
	NOT_SELECT_UNREGISTERED_MEMBER_TOWN(HttpStatus.BAD_REQUEST, "회원이 등록한 동네만 선택할 수 있습니다."),
	NOT_FOUND_MEMBER_TOWN(HttpStatus.NOT_FOUND, "회원 동네를 찾을 수 없습니다."),
	ALREADY_ADDRESS_NAME(HttpStatus.CONFLICT, "이미 존재하는 동네입니다."),
	FAIL_REMOVE_ADDRESS(HttpStatus.INTERNAL_SERVER_ERROR, "동네 삭제를 실패하였습니다."),

	// Oauth
	WRONG_AUTHORIZATION_CODE(HttpStatus.BAD_REQUEST, "잘못된 인가 코드입니다."),
	NOT_FOUND_PROVIDER(HttpStatus.NOT_FOUND, "provider를 찾을 수 없습니다."),
	ALREADY_LOGOUT(HttpStatus.UNAUTHORIZED, "이미 로그아웃 상태입니다."),
	NOT_LOGIN_STATE(HttpStatus.UNAUTHORIZED, "로그인 상태가 아닙니다."),
	FAIL_LOGIN(HttpStatus.UNAUTHORIZED, "로그인 정보가 일치하지 않습니다."),
	ALREADY_SIGNUP(HttpStatus.UNAUTHORIZED, "이미 회원가입된 상태입니다."),

	// Region
	NOT_FOUND_REGION(HttpStatus.NOT_FOUND, "주소를 찾지 못하였습니다."),

	// Sales
	INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

	// Wish
	DUPLICATED_REQUEST(HttpStatus.BAD_REQUEST, "이미 처리된 요청입니다."),

	// ChatLog
	NOT_FOUND_CHAT_LOG(HttpStatus.NOT_FOUND, "채팅이 존재하지 않습니다."),

	// ChatRoom
	NOT_FOUND_CHATROOM(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	@Override
	public String toString() {
		return String.format("%s, %s(name=%s, httpStatus=%s, message=%s)", "에러 코드", this.getClass().getSimpleName(),
			name(),
			httpStatus,
			message);
	}
}
