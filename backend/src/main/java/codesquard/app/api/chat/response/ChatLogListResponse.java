package codesquard.app.api.chat.response;

import java.util.Collections;
import java.util.List;

import codesquard.app.domain.item.Item;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ChatLogListResponse {

	private String chatPartnerName;
	private ChatLogItemResponse item;
	private List<ChatLogMessageResponse> chat;
	private Long nextMessageId;

	public static ChatLogListResponse emptyResponse(String chatPartnerName, Item item) {
		return new ChatLogListResponse(chatPartnerName, ChatLogItemResponse.from(item), Collections.emptyList(), null);
	}

	public boolean isEmptyChat() {
		return chat.isEmpty();
	}

	@Override
	public String toString() {
		return String.format("%s, %s(chatPartnerName=%s, item=%s, chat=%s)", "채팅 메시지 목록 응답",
			this.getClass().getSimpleName(), chatPartnerName, item, chat);
	}
}
