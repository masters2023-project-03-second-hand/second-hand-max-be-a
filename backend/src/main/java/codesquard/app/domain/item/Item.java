package codesquard.app.domain.item;

import java.time.LocalDateTime;
import java.util.Objects;

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

import codesquard.app.api.errors.errorcode.ItemErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.oauth.support.Principal;
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
		LocalDateTime createdAt, Long viewCount, Member member) {
		return Item.builder()
			.title(title)
			.content(content)
			.price(price)
			.status(status)
			.region(region)
			.createdAt(createdAt)
			.viewCount(viewCount)
			.member(member)
			.build();
	}

	public void changeCategory(Category category) {
		this.category = category;
	}

	public void changeBy(Item changeItem) {
		this.title = changeItem.title;
		this.price = changeItem.price;
		this.content = changeItem.content;
		this.region = changeItem.region;
		this.status = changeItem.status;
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

	@Override
	public String toString() {
		return String.format("%s, %s(id=%d, title=%s, price=%d, status=%s, region=%s, viewCount=%d)",
			"상품", this.getClass().getSimpleName(), id, title, price, status, region, viewCount);
	}

	public void validateAuthorization(Principal writer) {
		if (!Objects.equals(member.getId(), writer.getMemberId())) {
			throw new RestApiException(ItemErrorCode.ITEM_FORBIDDEN);
		}
	}
}
