package codesquard.app.api.chat.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import codesquard.app.domain.chat.ChatRoom;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomCreateResponse {

	@JsonProperty(value = "chatRoomId")
	private Long id;

	public static ChatRoomCreateResponse from(ChatRoom chatRoom) {
		return new ChatRoomCreateResponse(chatRoom.getId());
	}
}
