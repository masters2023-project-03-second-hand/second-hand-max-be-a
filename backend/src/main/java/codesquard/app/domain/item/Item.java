package codesquard.app.domain.item;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import codesquard.app.api.item.ItemRegisterRequest;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.image.Image;
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
	@OneToMany
	private List<Image> images;
	private LocalDateTime modifiedAt;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	public Item(String title, String content, Long price, ItemStatus status, String region, Member member,
		LocalDateTime createdAt) {
		this.title = title;
		this.content = content;
		this.price = price;
		this.status = status;
		this.region = region;
		this.member = member;
		this.createdAt = createdAt;
	}

	public static Item toEntity(ItemRegisterRequest request, Member member) {
		return new Item(
			request.getTitle(),
			request.getContent(),
			request.getPrice(),
			ItemStatus.of(request.getStatus()),
			request.getRegion(),
			member,
			LocalDateTime.now());
	}
}
