package codesquard.app.domain.wish;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import codesquard.app.domain.item.Item;
import codesquard.app.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Wish {

	@Id
	@GeneratedValue
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "member_id")
	private Member member;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "item_id")
	private Item item;
	private LocalDateTime createdAt;

	public Wish(Long memberId, Long itemId) {
		this.member = new Member(memberId);
		this.item = new Item(itemId);
		this.createdAt = LocalDateTime.now();
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public void setItem(Item item) {
		this.item = item;
		if (!item.getWishes().contains(this)) {
			this.item.addWish(this);
		}
	}

	public static Wish create(LocalDateTime createdAt) {
		return new Wish(null, null, null, createdAt);
	}
}
