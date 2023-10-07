package codesquard.app.api.chat;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import codesquard.app.api.chat.response.ChatRoomCreateResponse;
import codesquard.app.api.chat.response.ChatRoomListResponse;
import codesquard.app.api.response.ApiResponse;
import codesquard.app.api.success.successcode.ChatRoomSuccessCode;
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
		return ApiResponse.success(ChatRoomSuccessCode.CREATED_CHAT_ROOM, response);
	}

	@GetMapping("/chats")
	public ApiResponse<ChatRoomListResponse> readAllChatRoom(
		@PageableDefault Pageable pageable,
		@AuthPrincipal Principal principal) {
		ChatRoomListResponse response = chatRoomService.readAllChatRoom(principal, pageable);
		return ApiResponse.success(ChatRoomSuccessCode.OK_CHAT_ROOMS, response);
	}

	@GetMapping("/items/{itemId}/chats")
	public ApiResponse<ChatRoomListResponse> readAllChatRoomByItem(
		@PathVariable Long itemId,
		@PageableDefault Pageable pageable,
		@AuthPrincipal Principal principal) {
		ChatRoomListResponse response = chatRoomService.readAllChatRoomByItem(itemId, principal, pageable);
		return ApiResponse.success(ChatRoomSuccessCode.OK_CHAT_ROOMS_BY_ITEMS, response);
	}
}
