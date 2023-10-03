package codesquard.app.domain.chat;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member buyer;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id")
	private Item item;

	public ChatRoom(Member buyer, Item item) {
		this.createdAt = LocalDateTime.now();
		this.buyer = buyer;
		this.item = item;
	}

	public String getBuyerLoginId() {
		return buyer.getLoginId();
	}

	public String getSellerLoginId() {
		return item.getMember().getLoginId();
	}

	public Member getSeller() {
		return item.getMember();
	}

	@Override
	public String toString() {
		return String.format("%s, %s(id=%d, buyerLoginId=%s, itemTitle=%s)", "회원", this.getClass().getSimpleName(), id,
			buyer.getLoginId(), item.getTitle());
	}

}
