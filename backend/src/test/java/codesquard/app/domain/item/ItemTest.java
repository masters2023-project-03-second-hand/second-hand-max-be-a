package codesquard.app.domain.item;

import static codesquard.app.api.category.CategoryFixedFactory.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import codesquard.app.IntegrationTestSupport;
import codesquard.app.api.item.ItemFixedFactory;
import codesquard.app.api.oauth.OauthFixedFactory;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.member.Member;

class ItemTest extends IntegrationTestSupport {

	@DisplayName("상품에 카테고리를 설정한다")
	@Test
	public void setCategory() {
		// given
		Category category = createdFixedCategory();
		categoryRepository.save(category);

		Member member = OauthFixedFactory.createFixedMember();
		memberRepository.save(member);

		Item item = ItemFixedFactory.createFixedItem(member, null, 0L);

		// when
		item.changeCategory(category);

		// then
		Item saveItem = itemRepository.save(item);
		Assertions.assertThat(saveItem.getCategory()).isEqualTo(category);
	}

}
