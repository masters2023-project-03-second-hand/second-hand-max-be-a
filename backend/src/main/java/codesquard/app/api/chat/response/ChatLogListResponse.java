package codesquard.app.api.chat.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

	@JsonIgnore
	public boolean isEmptyChat() {
		return chat.isEmpty();
	}

	@Override
	public String toString() {
		return String.format("%s, %s(chatPartnerName=%s, item=%s, chat=%s)", "채팅 메시지 목록 응답",
			this.getClass().getSimpleName(), chatPartnerName, item, chat);
	}
}
