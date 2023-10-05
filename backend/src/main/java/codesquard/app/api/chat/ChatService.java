package codesquard.app.api.chat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import codesquard.app.api.chat.response.ChatLogListResponse;
import codesquard.app.api.response.ApiResponse;
import codesquard.app.domain.oauth.support.Principal;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ChatService {

	private static final int DEFAULT_READ_MESSAGE_SIZE = 10;

	private final Map<DeferredResult<ApiResponse<ChatLogListResponse>>, Long> chatRequests =
		new ConcurrentHashMap<>();

	private final ChatLogService chatLogService;

	public void onMessage(Long chatRoomId, Principal sender) {
		for (Map.Entry<DeferredResult<ApiResponse<ChatLogListResponse>>, Long> entry : this.chatRequests.entrySet()) {
			DeferredResult<ApiResponse<ChatLogListResponse>> key = entry.getKey();
			Long cursor = entry.getValue();
			key.setResult(ApiResponse.ok("채팅 메시지 목록 조회가 완료되었습니다.",
				chatLogService.readMessages(chatRoomId, sender, cursor, Pageable.ofSize(DEFAULT_READ_MESSAGE_SIZE))));
		}
	}

	public void putMessageIndex(DeferredResult<ApiResponse<ChatLogListResponse>> deferredResult, Long messageIndex) {
		chatRequests.put(deferredResult, messageIndex);
	}

	public void removeMessageIndex(DeferredResult<ApiResponse<ChatLogListResponse>> deferredResult) {
		chatRequests.remove(deferredResult);
	}
}
