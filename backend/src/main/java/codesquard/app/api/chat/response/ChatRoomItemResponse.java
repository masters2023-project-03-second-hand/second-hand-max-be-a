package codesquard.app.api.chat.response;

import java.time.LocalDateTime;

import codesquard.app.domain.chat.ChatLog;
import codesquard.app.domain.chat.ChatRoom;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomItemResponse {

	private Long chatRoomId;
	private String thumbnailUrl;
	private String chatPartnerName;
	private String chatPartnerProfile;
	private LocalDateTime lastSendTime;
	private String lastSendMessage;
	private Long newMessageCount;

	public ChatRoomItemResponse(Long chatRoomId, String thumbnailUrl, String chatPartnerName, String chatPartnerProfile,
		LocalDateTime lastSendTime, String lastSendMessage, Long newMessageCount) {
		this.chatRoomId = chatRoomId;
		this.thumbnailUrl = thumbnailUrl;
		this.chatPartnerName = chatPartnerName;
		this.chatPartnerProfile = chatPartnerProfile;
		this.lastSendTime = lastSendTime;
		this.lastSendMessage = lastSendMessage;
		this.newMessageCount = newMessageCount;
	}

	public static ChatRoomItemResponse of(ChatRoom chatRoom, Item item, Member partner, ChatLog chatLog,
		Long newMessageCount) {
		return new ChatRoomItemResponse(
			chatRoom.getId(),
			item.getThumbnailUrl(),
			partner.getLoginId(),
			partner.getAvatarUrl(),
			chatLog.getCreatedAt(),
			chatLog.getMessage(),
			newMessageCount
		);
	}
}
