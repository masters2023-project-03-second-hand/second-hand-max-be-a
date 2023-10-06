package codesquard.app.api.chat;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import codesquard.app.api.chat.response.ChatLogListResponse;
import codesquard.app.api.response.ApiResponse;
import codesquard.app.domain.oauth.support.Principal;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ChatService {

	private final Map<DeferredResult<ApiResponse<ChatLogListResponse>>, Map<String, Object>> chatRequests = new ConcurrentHashMap<>();

	private final ChatLogService chatLogService;

	public void onMessage(Long chatRoomId) {
		for (Map.Entry<DeferredResult<ApiResponse<ChatLogListResponse>>, Map<String, Object>> entry : this.chatRequests.entrySet()) {
			DeferredResult<ApiResponse<ChatLogListResponse>> key = entry.getKey();
			Long cursor = (Long)entry.getValue().get("messageId");
			Principal requester = (Principal)entry.getValue().get("requester");
			key.setResult(ApiResponse.ok("채팅 메시지 목록 조회가 완료되었습니다.",
				chatLogService.readMessages(chatRoomId, requester, cursor)));
		}
	}

	public void putMessageIndex(DeferredResult<ApiResponse<ChatLogListResponse>> deferredResult, Long messageId,
		Principal requester) {
		Map<String, Object> map = new HashMap<>();
		map.put("messageId", messageId);
		map.put("requester", requester);
		chatRequests.put(deferredResult, map);
	}

	public void removeMessageIndex(DeferredResult<ApiResponse<ChatLogListResponse>> deferredResult) {
		chatRequests.remove(deferredResult);
	}
}
