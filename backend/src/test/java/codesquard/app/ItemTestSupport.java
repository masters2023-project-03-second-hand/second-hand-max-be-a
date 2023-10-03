package codesquard.app;

import static java.time.LocalDateTime.*;

import codesquard.app.domain.category.Category;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.item.ItemStatus;
import codesquard.app.domain.member.Member;

public class ItemTestSupport {

	public static Item createItem(String title, String content, Long price, ItemStatus status, String region,
		String thumbnailUrl, Member member, Category category) {
		return Item.builder()
			.title(title)
			.content(content)
			.price(price)
			.status(status)
			.region(region)
			.thumbnailUrl(thumbnailUrl)
			.createdAt(now())
			.wishCount(0L)
			.viewCount(0L)
			.chatCount(0L)
			.member(member)
			.category(category)
			.build();
	}
}
