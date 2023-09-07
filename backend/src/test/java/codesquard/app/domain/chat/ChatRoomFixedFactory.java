package codesquard.app.domain.chat;

import java.time.LocalDateTime;

public class ChatRoomFixedFactory {
	public static ChatRoom createFixedChatRoom() {
		return ChatRoom.create(LocalDateTime.now());
	}
}
