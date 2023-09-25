package codesquard.app.api.chat.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import codesquard.app.domain.chat.ChatLog;
import codesquard.app.domain.oauth.support.Principal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatLogMessageResponse {
	private int messageIndex;
	@JsonProperty("isMe")
	private boolean isMe;
	private String message;

	public static ChatLogMessageResponse from(int messageIndex, ChatLog chatLog, Principal principal) {
		boolean isMe = chatLog.isSender(principal.getLoginId());
		return new ChatLogMessageResponse(messageIndex, isMe, chatLog.getMessage());
	}

	@Override
	public String toString() {
		return String.format("%s, %s(messageIndex=%d, isMe=%s, message=%s)", "채팅 메시지 응답",
			this.getClass().getSimpleName(), messageIndex, isMe, message);
	}
}
