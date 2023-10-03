package codesquard.app.domain.chat;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import codesquard.app.domain.oauth.support.Principal;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChatLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String message;
	private String sender;
	private String receiver;
	private LocalDateTime createdAt;
	private int readCount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chat_room_id")
	private ChatRoom chatRoom;

	public ChatLog(String message, String sender, String receiver, ChatRoom chatRoom, int readCount) {
		this.message = message;
		this.sender = sender;
		this.receiver = receiver;
		this.createdAt = LocalDateTime.now();
		this.chatRoom = chatRoom;
		this.readCount = readCount;
	}

	public static ChatLog createBySender(String message, ChatRoom chatRoom, Principal sender) {
		if (sender.isBuyer(chatRoom.getBuyer())) {
			return new ChatLog(message, sender.getLoginId(), chatRoom.getSellerLoginId(), chatRoom, 1);
		}
		return new ChatLog(message, sender.getLoginId(), chatRoom.getBuyerLoginId(), chatRoom, 1);
	}

	public boolean isSender(String loginId) {
		return sender.equals(loginId);
	}

	public void decreaseMessageReadCount(String readerLoginId) {
		if (this.readCount == 0 || this.sender.equals(readerLoginId)) {
			return;
		}
		this.readCount--;
	}
}
