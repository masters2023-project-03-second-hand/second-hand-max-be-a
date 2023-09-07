package codesquard.app.api.item;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;

import codesquard.app.domain.category.Category;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.item.ItemStatus;
import codesquard.app.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRegisterRequest {

	@NotBlank(message = "제목을 입력해주세요.")
	private String title;
	private Long price;
	private String content;
	private String region;
	private String status;
	private Long categoryId;
	private String categoryName;

	public Item toEntity(Member member, String thumbnailUrl) {
		Item item = Item.builder()
			.title(title)
			.content(content)
			.price(price)
			.status(ItemStatus.of(status))
			.region(region)
			.createdAt(LocalDateTime.now())
			.thumbnailUrl(thumbnailUrl)
			.wishCount(0L)
			.chatCount(0L)
			.viewCount(0L)
			.build();
		item.setMember(member);
		item.setCategory(new Category(categoryId));
		return item;
	}

}
