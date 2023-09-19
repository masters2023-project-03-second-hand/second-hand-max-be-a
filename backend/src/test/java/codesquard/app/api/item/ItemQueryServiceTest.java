package codesquard.app.api.item;

import static codesquard.app.CategoryTestSupport.*;
import static codesquard.app.MemberTestSupport.*;
import static codesquard.app.domain.item.ItemStatus.*;
import static java.time.LocalDateTime.*;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import codesquard.app.CategoryTestSupport;
import codesquard.app.IntegrationTestSupport;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.item.response.ItemDetailResponse;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.image.Image;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.wish.Wish;

class ItemQueryServiceTest extends IntegrationTestSupport {

	private Member member;

	@BeforeEach
	void setup() {
		member = createMember("avatarUrlValue", "23Yong@gmail.com", "23Yong");
		memberRepository.save(member);

		List<Category> categories = getCategories();
		categoryRepository.saveAll(categories);
	}

	@DisplayName("판매자가 한 상품의 상세한 정보를 조회한다")
	@Test
	public void findDetailItemBySeller() {
		// given
		Category category = CategoryTestSupport.findByName("스포츠/레저");
		Item item = Item.builder()
			.title("빈티지 롤러 블레이드")
			.content("어린시절 추억의향수를 불러 일으키는 롤러 스케이트입니다.")
			.price(200000L)
			.status(ON_SALE)
			.region("가락동")
			.createdAt(now())
			.wishCount(0L)
			.viewCount(0L)
			.chatCount(0L)
			.member(member)
			.category(category)
			.build();
		Item saveItem = itemRepository.save(item);

		List<Image> images = List.of(
			new Image("imageUrlValue1", new Item(saveItem.getId())),
			new Image("imageUrlValue2", new Item(saveItem.getId())));
		imageRepository.saveAll(images);

		Wish wish = new Wish(member, item, now());
		wishRepository.save(wish);

		// when
		ItemDetailResponse response = itemQueryService.findDetailItemBy(item.getId(), member.getId());
		// then
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(response)
				.extracting("isSeller", "seller", "status", "title", "categoryName", "content", "chatCount",
					"wishCount", "viewCount", "price")
				.contains(true, "23Yong", "판매중", "빈티지 롤러 블레이드", "스포츠/레저", "어린시절 추억의향수를 불러 일으키는 롤러 스케이트입니다.", 0L, 0L, 0L,
					200000L);
			softAssertions.assertThat(response.getImageUrls())
				.hasSize(2);
			softAssertions.assertAll();
		});
	}

	@DisplayName("존재하지 않는 상품 등록번호로 상품을 조회할 수 없다")
	@Test
	public void findDetailItemWithNotExistItem() {
		// given
		Member member = createMember("avatarUrlValue", "23Yong@gmail.com", "23Yong");
		Long itemId = 9999L;
		// when
		Throwable throwable = Assertions.catchThrowable(
			() -> itemQueryService.findDetailItemBy(itemId, member.getId()));
		// then
		Assertions.assertThat(throwable)
			.isInstanceOf(RestApiException.class)
			.extracting("errorCode.message")
			.isEqualTo("상품을 찾을 수 없습니다.");
	}
}
