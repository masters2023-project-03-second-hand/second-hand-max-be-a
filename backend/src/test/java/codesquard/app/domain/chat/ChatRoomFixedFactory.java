package codesquard.app.domain.chat;

import java.time.LocalDateTime;

import codesquard.app.domain.item.Item;
import codesquard.app.domain.member.Member;

public class ChatRoomFixedFactory {
	public static ChatRoom createFixedChatRoom(Member member, Item item) {
		return ChatRoom.create(LocalDateTime.now(), member, item);
	}
}
