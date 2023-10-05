package codesquard.app.api.chat.response;

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
	private Long messageId;
	private Boolean isMe;
	private String message;

	public static ChatLogMessageResponse from(ChatLog chatLog, Principal principal) {
		boolean isMe = chatLog.isSender(principal.getLoginId());
		return new ChatLogMessageResponse(chatLog.getId(), isMe, chatLog.getMessage());
	}

	@Override
	public String toString() {
		return String.format("%s, %s(isMe=%s, message=%s)", "채팅 메시지 응답",
			this.getClass().getSimpleName(), isMe, message);
	}
}
