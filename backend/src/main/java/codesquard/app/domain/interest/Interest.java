package codesquard.app.domain.interest;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import codesquard.app.domain.item.Item;
import codesquard.app.domain.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Interest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "member_id")
	private Member member;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "item_id")
	private Item item;
	private LocalDateTime createdAt;

	private Interest(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	//== 연관관계 메소드==//
	public void setMember(Member member) {
		this.member = member;
		if (!member.getInterests().contains(this)) {
			this.member.addInterest(this);
		}
	}

	public void setItem(Item item) {
		this.item = item;
		if (!item.getInterests().contains(this)) {
			this.item.addInterest(this);
		}
	}

	public static Interest create(LocalDateTime createdAt) {
		return new Interest(createdAt);
	}

}
