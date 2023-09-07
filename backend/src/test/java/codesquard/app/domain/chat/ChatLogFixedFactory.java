package codesquard.app.domain.chat;

import java.time.LocalDateTime;

public class ChatLogFixedFactory {
	public static ChatLog createFixedChatLog(ChatRoom chatRoom) {
		ChatLog chatLog = ChatLog.create("hello", "sam", "23Yong", LocalDateTime.now());
		chatLog.setChatRoom(chatRoom);
		return chatLog;
	}
}
