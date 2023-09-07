package codesquard.app.domain.chat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import codesquard.app.domain.item.Item;
import codesquard.app.domain.member.Member;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@Table(name = "chat_room")
@Entity
public class ChatRoom {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private LocalDateTime createdAt;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "member_id")
	private Member member;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "item_id")
	private Item item;

	@OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
	private List<ChatLog> chatLogs = new ArrayList<>();

	private ChatRoom(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public static ChatRoom create(LocalDateTime createdAt) {
		return new ChatRoom(createdAt);
	}

	public void setMember(Member member) {
		this.member = member;
		if (member != null && !member.getChatRooms().contains(this)) {
			member.addChatRoom(this);
		}
	}

	public void setItem(Item item) {
		this.item = item;
		if (item != null && !item.getChatRooms().contains(this)) {
			item.addChatRoom(this);
		}
	}

	public void addChatLog(ChatLog chatLog) {
		if (chatLog != null && !chatLogs.contains(chatLog)) {
			chatLogs.add(chatLog);
		}
		if (chatLog != null) {
			chatLog.setChatRoom(this);
		}
	}

	public int getChatLogsSize() {
		return chatLogs.size();
	}
}
