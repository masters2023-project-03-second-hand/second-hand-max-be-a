package codesquard.app.api.item.request;

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
	private ItemStatus status;
	private Long categoryId;
	private String categoryName;

	public Item toEntity(Member member, String thumbnailUrl) {
		return Item.builder()
			.title(title)
			.content(content)
			.price(price)
			.status(status)
			.region(region)
			.createdAt(LocalDateTime.now())
			.thumbnailUrl(thumbnailUrl)
			.wishCount(0L)
			.chatCount(0L)
			.viewCount(0L)
			.member(member)
			.category(new Category(categoryId))
			.build();
	}
}
