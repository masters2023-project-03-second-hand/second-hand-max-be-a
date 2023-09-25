package codesquard.app.api.chat.response;

import codesquard.app.domain.item.Item;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatLogItemResponse {
	private String title;
	private String thumbnailUrl;
	private Long price;

	public static ChatLogItemResponse from(Item item) {
		return new ChatLogItemResponse(item.getTitle(), item.getThumbnailUrl(), item.getPrice());
	}

	@Override
	public String toString() {
		return String.format("%s, %s(title=%s, price=%d)", "채팅 메시지 아이템 응답", this.getClass().getSimpleName(), title,
			price);
	}
}
