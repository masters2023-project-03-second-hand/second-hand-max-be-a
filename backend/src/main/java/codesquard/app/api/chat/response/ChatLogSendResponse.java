package codesquard.app.api.chat.response;

import java.time.LocalDateTime;

import codesquard.app.domain.chat.ChatLog;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatLogSendResponse {

	private Long id;
	private String message;
	private String sender;
	private String receiver;
	private LocalDateTime createdAt;

	public static ChatLogSendResponse from(ChatLog chatLog) {
		return new ChatLogSendResponse(
			chatLog.getId(),
			chatLog.getMessage(),
			chatLog.getSender(),
			chatLog.getReceiver(),
			chatLog.getCreatedAt());
	}
}
