package codesquard.app.api.chat;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import codesquard.app.api.chat.response.ChatRoomCreateResponse;
import codesquard.app.api.response.ApiResponse;
import codesquard.app.domain.oauth.support.AuthPrincipal;
import codesquard.app.domain.oauth.support.Principal;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class ChatRoomRestController {

	private final ChatRoomService chatRoomService;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/items/{itemId}/chats")
	public ApiResponse<ChatRoomCreateResponse> createChatRoom(
		@PathVariable Long itemId,
		@AuthPrincipal Principal buyer) {
		ChatRoomCreateResponse response = chatRoomService.createChatRoom(itemId, buyer);
		return ApiResponse.created("채팅방 생성을 완료하였습니다.", response);
	}
}
