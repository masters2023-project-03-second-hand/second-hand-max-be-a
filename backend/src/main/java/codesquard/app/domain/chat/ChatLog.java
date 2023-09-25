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
	private boolean isRead;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chat_room_id")
	private ChatRoom chatRoom;

	public ChatLog(String message, String sender, String receiver, ChatRoom chatRoom, boolean isRead) {
		this.message = message;
		this.sender = sender;
		this.receiver = receiver;
		this.createdAt = LocalDateTime.now();
		this.chatRoom = chatRoom;
		this.isRead = isRead;
	}

	public boolean isSender(String loginId) {
		return sender.equals(loginId);
	}
}
