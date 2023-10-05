package codesquard.app.api.chat.response;

import codesquard.app.domain.chat.ChatRoom;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomCreateResponse {
	
	private Long chatRoomId;

	public static ChatRoomCreateResponse from(ChatRoom chatRoom) {
		return new ChatRoomCreateResponse(chatRoom.getId());
	}
}
