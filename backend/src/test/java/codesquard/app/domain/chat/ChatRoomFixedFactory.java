package codesquard.app.domain.chat;

import java.time.LocalDateTime;

import codesquard.app.domain.member.Member;

public class ChatRoomFixedFactory {
	public static ChatRoom createFixedChatRoom(Member member) {
		return ChatRoom.create(LocalDateTime.now(), member);
	}
}
