package codesquard.app.api.wishitem;

import static org.assertj.core.api.Assertions.*;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import codesquard.app.api.item.ItemRegisterRequest;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.member.Member;
import codesquard.support.SupportRepository;

@SpringBootTest
class WishItemServiceTest {

	@Autowired
	private WishItemService wishItemService;
	@Autowired
	private EntityManager em;
	@Autowired
	private SupportRepository supportRepository;

	@Test
	@DisplayName("관심상품 등록에 성공한다.")
	void wishRegisterTest() {

		// given
		ItemRegisterRequest request = new ItemRegisterRequest(
			"선풍기", 12000L, null, "가양 1동", "판매중", 1L, null);

		Member member = supportRepository.save(Member.create("avatar", "pie@pie", "pieeeeeee"));
		Item item1 = supportRepository.save(Item.toEntity(request, member, "thumbnail"));

		// when
		wishItemService.register(item1.getId(), member.getId());

		// then
		Item item = em.find(Item.class, item1.getId());
		assertThat(item.getWishCount()).isEqualTo(1);
	}

	@Test
	@DisplayName("관심상품 취소에 성공한다.")
	void wishCancelTest() {

		// given
		ItemRegisterRequest request1 = new ItemRegisterRequest(
			"선풍기", 12000L, null, "가양 1동", "판매중", 1L, null);
		Member member = supportRepository.save(Member.create("avatar", "pie@pie", "piepie"));
		Item saveItem = supportRepository.save(Item.toEntity(request1, member, "thumbnail"));
		wishItemService.register(saveItem.getId(), member.getId());

		// when
		wishItemService.cancel(saveItem.getId());

		// then
		Item item = em.find(Item.class, saveItem.getId());
		assertThat(item.getWishCount()).isEqualTo(0);
	}
}
