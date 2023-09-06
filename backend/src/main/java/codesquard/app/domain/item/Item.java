package codesquard.app.domain.item;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import codesquard.app.api.item.ItemRegisterRequest;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String title;
	private String content;
	private Long price;
	private ItemStatus status;
	private String region;
	private LocalDateTime createdAt;
	private String thumbnailUrl;
	private LocalDateTime modifiedAt;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;
	private Long wishCount = 0L;
	private Long chatCount = 0L;
	private Long viewCount = 0L;

	public Item(String title, String content, Long price, ItemStatus status, String region, Member member,
		LocalDateTime createdAt, String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
		this.title = title;
		this.content = content;
		this.price = price;
		this.status = status;
		this.region = region;
		this.member = member;
		this.createdAt = createdAt;
	}

	public Item(Long id) {
		this.id = id;
	}

	public static Item toEntity(ItemRegisterRequest request, Member member, String thumbnailUrl) {
		return new Item(
			request.getTitle(),
			request.getContent(),
			request.getPrice(),
			ItemStatus.of(request.getStatus()),
			request.getRegion(),
			member,
			LocalDateTime.now(),
			thumbnailUrl);
	}

	public void wishRegister() {
		this.wishCount++;
	}

	public void wishCancel() {
		this.wishCount--;
	}
}
