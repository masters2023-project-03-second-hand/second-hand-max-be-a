package codesquard.app.api.chat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

import codesquard.app.annotation.MessageIndex;
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

	private final Map<DeferredResult<ChatLogListResponse>, Integer> chatRequests = new ConcurrentHashMap<>();

	private final ChatLogService chatLogService;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/chats/{chatRoomId}")
	public ApiResponse<ChatLogSendResponse> sendMessage(
		@PathVariable Long chatRoomId,
		@RequestBody ChatLogSendRequest request,
		@AuthPrincipal Principal sender) {
		ChatLogSendResponse response = chatLogService.sendMessage(request, chatRoomId, sender);

		this.chatRequests.forEach((key, messageIndex) ->
			key.setResult(chatLogService.readMessages(chatRoomId, messageIndex, sender)));
		return ApiResponse.created("메시지 전송이 완료되었습니다.", response);
	}

	@GetMapping("/chats/{chatRoomId}")
	public DeferredResult<ChatLogListResponse> readMessages(
		@PathVariable Long chatRoomId,
		@RequestParam(required = false, defaultValue = "0") @MessageIndex int messageIndex,
		@AuthPrincipal Principal principal) {
		log.info("메시지 읽기 요청 : chatRoomId={}, messageIndex={}, 요청한 아이디={}", chatRoomId, messageIndex,
			principal.getLoginId());
		DeferredResult<ChatLogListResponse> deferredResult = new DeferredResult<>(1000L);
		this.chatRequests.put(deferredResult, messageIndex);

		deferredResult.onCompletion(() -> chatRequests.remove(deferredResult));
		deferredResult.onTimeout(() -> deferredResult.setErrorResult(
			ApiResponse.of(HttpStatus.REQUEST_TIMEOUT, "새로운 채팅 메시지가 존재하지 않습니다.", null)));

		ChatLogListResponse response = chatLogService.readMessages(chatRoomId, messageIndex, principal);

		if (!response.isEmptyChat()) {
			deferredResult.setResult(response);
		}
		return deferredResult;
	}
}
