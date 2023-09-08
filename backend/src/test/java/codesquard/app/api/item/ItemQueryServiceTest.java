package codesquard.app.api.item;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import codesquard.app.IntegrationTestSupport;
import codesquard.app.api.category.CategoryFixedFactory;
import codesquard.app.api.errors.errorcode.CategoryErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.item.response.ItemDetailResponse;
import codesquard.app.api.oauth.OauthFixedFactory;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.image.Image;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.wish.Wish;

class ItemQueryServiceTest extends IntegrationTestSupport {

	@DisplayName("판매자가 한 상품의 상세한 정보를 조회한다")
	@Test
	public void findDetailItemBySeller() {
		// given
		List<Category> categories = CategoryFixedFactory.createFixedCategories();
		categoryRepository.saveAll(categories);
		Category findCategory = categories.stream()
			.filter(category -> category.getName().equals("가구/인테리어"))
			.findAny()
			.orElseThrow(() -> new RestApiException(CategoryErrorCode.NOT_FOUND_CATEGORY));

		Member member = OauthFixedFactory.createFixedMemberWithMemberTown();
		memberRepository.save(member);

		List<Image> images = ImageFixedFactory.createFixedImages();
		long viewCount = 4L;
		List<Wish> wishes = List.of(
			WishFixedFactory.createWish(member),
			WishFixedFactory.createWish(member),
			WishFixedFactory.createWish(member)
		);
		Item item = ItemFixedFactory.createFixedItem(member, findCategory, images, wishes, viewCount);
		itemRepository.save(item);
		wishRepository.saveAll(wishes);
		imageRepository.saveAll(images);
		// when
		ItemDetailResponse response = itemQueryService.findDetailItemBy(item.getId(), member.getId());
		// then
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(response)
				.extracting("isSeller", "seller", "status", "title", "categoryName", "content", "chatCount",
					"wishCount", "viewCount", "price")
				.contains(true, "23Yong", "판매중", "빈티지 롤러 스케이트", "가구/인테리어", "어린시절 추억의향수를 불러 일으키는 롤러 스케이트입니다.", 0, 3, 4,
					169000);
			softAssertions.assertThat(response.getImageUrls())
				.hasSize(2);
			softAssertions.assertAll();
		});
	}

	@DisplayName("존재하지 않는 상품 등록번호로 상품을 조회할 수 없다")
	@Test
	public void findDetailItemWithNotExistItem() {
		// given
		Member member = OauthFixedFactory.createFixedMemberWithMemberTown();
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
