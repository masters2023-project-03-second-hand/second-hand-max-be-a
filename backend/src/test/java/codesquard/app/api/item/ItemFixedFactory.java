package codesquard.app.api.item;

import java.time.LocalDateTime;
import java.util.List;

import codesquard.app.domain.category.Category;
import codesquard.app.domain.image.Image;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.item.ItemStatus;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.wish.Wish;

public class ItemFixedFactory {

	private static final String TITLE = "빈티지 롤러 스케이트";
	private static final String CONTENT = "어린시절 추억의향수를 불러 일으키는 롤러 스케이트입니다.";
	private static final Long PRICE = 169000L;
	private static final ItemStatus STATUS = ItemStatus.ON_SALE;
	private static final String REGION = "가락 1동";
	private static final LocalDateTime CREATED_AT = LocalDateTime.of(2023, 1, 1, 0, 0);

	public static Item createFixedItem(Member member, Category category, List<Image> images, List<Wish> wishes,
		Long viewCount) {
		Item item = Item.create(TITLE, CONTENT, PRICE, STATUS, REGION, CREATED_AT, viewCount);
		item.setMember(member);
		item.setCategory(category);
		images.forEach(item::addImage);
		wishes.forEach(item::addWish);
		return item;
	}
}
