package codesquard.app.domain.chat;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chat_room_id")
	private ChatRoom chatRoom;

	private ChatLog(String message, String sender, String receiver, LocalDateTime createdAt) {
		this.message = message;
		this.sender = sender;
		this.receiver = receiver;
		this.createdAt = createdAt;
	}

	public static ChatLog create(String message, String sender, String receiver, LocalDateTime createdAt) {
		return new ChatLog(message, sender, receiver, createdAt);
	}

	public void changeChatRoom(ChatRoom chatRoom) {
		this.chatRoom = chatRoom;
		addChatLogBy(chatRoom);
	}

	private void addChatLogBy(ChatRoom chatRoom) {
		if (chatRoom == null) {
			return;
		}
		if (!chatRoom.containsChatLog(this)) {
			chatRoom.addChatLog(this);
		}
	}

}
