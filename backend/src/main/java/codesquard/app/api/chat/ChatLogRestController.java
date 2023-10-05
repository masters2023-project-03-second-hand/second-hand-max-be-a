package codesquard.app.api.chat;

import java.util.Collections;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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
import codesquard.app.domain.oauth.support.AuthPrincipal;
import codesquard.app.domain.oauth.support.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
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
		chatService.onMessage(chatRoomId, sender);
		return ApiResponse.created("메시지 전송이 완료되었습니다.", response);
	}

	@GetMapping("/chats/{chatRoomId}")
	public DeferredResult<ApiResponse<ChatLogListResponse>> readMessages(
		@PathVariable Long chatRoomId,
		@RequestParam(required = false, defaultValue = "0") Long messageId,
		@PageableDefault Pageable pageable,
		@AuthPrincipal Principal principal) {
		log.info("메시지 읽기 요청 : chatRoomId={}, cursor={}, pageable={}, 요청한 아이디={}", chatRoomId, messageId, pageable,
			principal.getLoginId());

		DeferredResult<ApiResponse<ChatLogListResponse>> deferredResult = new DeferredResult<>(10000L);
		chatService.putMessageIndex(deferredResult, messageId);

		deferredResult.onCompletion(() -> chatService.removeMessageIndex(deferredResult));
		deferredResult.onTimeout(() -> deferredResult.setErrorResult(
			ApiResponse.ok("새로운 채팅 메시지가 존재하지 않습니다.", Collections.emptyList())));

		ChatLogListResponse response = chatLogService.readMessages(chatRoomId, principal, messageId, pageable);

		if (!response.isEmptyChat()) {
			deferredResult.setResult(ApiResponse.ok("채팅 메시지 목록 조회가 완료되었습니다.", response));
		}
		return deferredResult;
	}
}
