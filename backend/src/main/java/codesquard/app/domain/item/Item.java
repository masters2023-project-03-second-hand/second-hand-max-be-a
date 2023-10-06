package codesquard.app.domain.item;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import codesquard.app.domain.category.Category;
import codesquard.app.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@DynamicInsert
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String title;
	private String content;
	private Long price;
	@Enumerated(value = EnumType.STRING)
	private ItemStatus status;
	private String region;
	private LocalDateTime createdAt;
	private String thumbnailUrl;
	private LocalDateTime modifiedAt;
	@ColumnDefault("0")
	private Long wishCount;
	@ColumnDefault("0")
	private Long chatCount;
	@ColumnDefault("0")
	private Long viewCount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	public Item(Long id) {
		this.id = id;
	}

	@Builder
	public Item(String title, String content, Long price, ItemStatus status, String region, LocalDateTime createdAt,
		String thumbnailUrl, LocalDateTime modifiedAt, Long wishCount, Long chatCount, Long viewCount, Member member,
		Category category) {
		this.title = title;
		this.content = content;
		this.price = price;
		this.status = status;
		this.region = region;
		this.createdAt = createdAt;
		this.thumbnailUrl = thumbnailUrl;
		this.modifiedAt = modifiedAt;
		this.wishCount = wishCount;
		this.chatCount = chatCount;
		this.viewCount = viewCount;
		this.member = member;
		this.category = category;
	}

	public static Item create(String title, String content, Long price, ItemStatus status, String region,
		LocalDateTime createdAt, Long wishCount, Long viewCount, Long chatCount, Member member, Category category) {
		return Item.builder()
			.title(title)
			.content(content)
			.price(price)
			.status(status)
			.region(region)
			.createdAt(createdAt)
			.wishCount(wishCount)
			.viewCount(viewCount)
			.chatCount(chatCount)
			.member(member)
			.category(category)
			.build();
	}

	public void change(Category category, Item changeItem, String thumbnailUrl) {
		this.category = category;
		this.title = changeItem.title;
		this.price = changeItem.price;
		this.content = changeItem.content;
		this.region = changeItem.region;
		this.status = changeItem.status;
		this.thumbnailUrl = thumbnailUrl;
	}

	public void changeStatus(ItemStatus status) {
		this.status = status;
	}

	public void wishRegister() {
		this.wishCount++;
	}

	public void wishCancel() {
		this.wishCount--;
	}

	public boolean isSeller(Long memberId) {
		return member.getId() == memberId;
	}

	public void increaseChatCount() {
		this.chatCount++;
	}

	@Override
	public String toString() {
		return String.format("%s, %s(id=%d, title=%s, price=%d, status=%s, region=%s, viewCount=%d)",
			"상품", this.getClass().getSimpleName(), id, title, price, status, region, viewCount);
	}

}
