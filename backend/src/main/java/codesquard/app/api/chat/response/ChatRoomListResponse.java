package codesquard.app.api.chat.response;

import java.util.List;

import codesquard.app.api.item.response.ItemResponses;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomListResponse {
	private List<ChatRoomItemResponse> contents;
	private ItemResponses.Paging paging;

	public ChatRoomListResponse(List<ChatRoomItemResponse> contents, boolean hasNext, Long nextCursor) {
		this.contents = contents;
		this.paging = ItemResponses.Paging.create(nextCursor, hasNext);
	}
}
