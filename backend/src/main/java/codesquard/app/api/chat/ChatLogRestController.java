package codesquard.app.api.chat;

import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import codesquard.app.api.chat.request.ChatLogSendRequest;
import codesquard.app.api.chat.response.ChatLogListResponse;
import codesquard.app.api.chat.response.ChatLogSendResponse;
import codesquard.app.api.response.ApiResponse;
import codesquard.app.api.success.successcode.ChatLogSuccessCode;
import codesquard.app.domain.oauth.support.AuthPrincipal;
import codesquard.app.domain.oauth.support.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class ChatLogRestController {

	private final ChatLogService chatLogService;
	private final ChatService chatService;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/chats/{chatRoomId}")
	public ApiResponse<ChatLogSendResponse> sendMessage(
		@PathVariable Long chatRoomId,
		@RequestBody ChatLogSendRequest request,
		@AuthPrincipal Principal sender) {
		ChatLogSendResponse response = chatLogService.sendMessage(request, chatRoomId, sender);
		chatService.onMessage(chatRoomId);
		return ApiResponse.success(ChatLogSuccessCode.CREATED_CHAT_LOG, response);
	}

	@GetMapping("/chats/{chatRoomId}")
	public DeferredResult<ApiResponse<ChatLogListResponse>> readMessages(
		@PathVariable Long chatRoomId,
		@RequestParam(required = false, defaultValue = "0") Long messageId,
		@AuthPrincipal Principal principal) {
		log.info("메시지 읽기 요청 : chatRoomId={}, cursor={}, 요청한 아이디={}", chatRoomId, messageId, principal.getLoginId());

		DeferredResult<ApiResponse<ChatLogListResponse>> deferredResult = new DeferredResult<>(10000L);
		chatService.putMessageIndex(deferredResult, messageId, principal);

		deferredResult.onCompletion(() -> chatService.removeMessageIndex(deferredResult));
		deferredResult.onTimeout(() -> deferredResult.setErrorResult(
			ApiResponse.success(ChatLogSuccessCode.OK_NOT_NEW_CHAT_LOG, Collections.emptyList())));

		ChatLogListResponse response = chatLogService.readMessages(chatRoomId, principal, messageId);

		if (!response.isEmptyChat()) {
			deferredResult.setResult(ApiResponse.success(ChatLogSuccessCode.OK_CHAT_LOGS, response));
		}
		return deferredResult;
	}
}
