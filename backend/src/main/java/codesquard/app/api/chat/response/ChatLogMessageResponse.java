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
	private int messageIndex;
	private boolean isMe;
	private String message;

	public static ChatLogMessageResponse from(int messageIndex, ChatLog chatLog, Principal principal) {
		boolean isMe = chatLog.isSender(principal.getLoginId());
		return new ChatLogMessageResponse(messageIndex, isMe, chatLog.getMessage());
	}
}
