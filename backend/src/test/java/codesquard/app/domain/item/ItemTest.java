package codesquard.app.domain.item;

import static codesquard.app.api.category.CategoryFixedFactory.*;

import java.util.ArrayList;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.IntegrationTestSupport;
import codesquard.app.api.item.ItemFixedFactory;
import codesquard.app.api.item.WishFixedFactory;
import codesquard.app.api.oauth.OauthFixedFactory;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.wish.Wish;

class ItemTest extends IntegrationTestSupport {

	@DisplayName("상품에 카테고리를 설정한다")
	@Test
	public void setCategory() {
		// given
		Category category = createdFixedCategory();
		categoryRepository.save(category);

		Member member = OauthFixedFactory.createFixedMember();
		memberRepository.save(member);

		Item item = ItemFixedFactory.createFixedItem(member, null, new ArrayList<>(), 0L);

		// when
		item.changeCategory(category);

		// then
		Item saveItem = itemRepository.save(item);
		Assertions.assertThat(saveItem.getCategory()).isEqualTo(category);
	}

	@Transactional
	@DisplayName("상품에 관심 상품을 추가한다")
	@Test
	public void addInterest() {
		// given
		Category category = createdFixedCategory();
		Member member = OauthFixedFactory.createFixedMember();

		Item item = ItemFixedFactory.createFixedItem(member, category, new ArrayList<>(),
			0L);

		Wish wish = WishFixedFactory.createWish(member);

		// when
		item.addWish(wish);

		// then
		categoryRepository.save(category);
		memberRepository.save(member);
		Item saveItem = itemRepository.save(item);
		itemRepository.findById(saveItem.getId()).orElseThrow();

		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(saveItem.getWishes()).hasSize(1).contains(wish);
			softAssertions.assertThat(wish.getItem()).isEqualTo(saveItem);
			softAssertions.assertAll();
		});
	}
}
