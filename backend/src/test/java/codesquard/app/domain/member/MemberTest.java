package codesquard.app.domain.member;

import java.util.ArrayList;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import codesquard.app.IntegrationTestSupport;
import codesquard.app.api.category.CategoryFixedFactory;
import codesquard.app.api.item.ItemFixedFactory;
import codesquard.app.api.oauth.OauthFixedFactory;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.item.Item;

class MemberTest extends IntegrationTestSupport {

	@Autowired
	private MemberRepository memberRepository;

	@DisplayName("회원이 상품을 추가한다")
	@Test
	public void addItem() {
		// given
		Member member = OauthFixedFactory.createFixedMember();
		Category category = CategoryFixedFactory.createdFixedCategory();
		categoryRepository.save(category);
		Item item = ItemFixedFactory.createFixedItem(member, category, new ArrayList<>(), new ArrayList<>(), 0L);
		// when
		member.addItem(item);
		// then
		Member saveMember = memberRepository.save(member);
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(saveMember.getItems().contains(item)).isTrue();
			softAssertions.assertThat(saveMember.getItems()).hasSize(1);
			softAssertions.assertThat(item.getMember()).isEqualTo(saveMember);
			softAssertions.assertAll();
		});
	}
}
