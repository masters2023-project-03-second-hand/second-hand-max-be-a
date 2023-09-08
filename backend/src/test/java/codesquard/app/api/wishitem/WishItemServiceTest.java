package codesquard.app.api.wishitem;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import codesquard.app.IntegrationTestSupport;
import codesquard.app.api.item.ItemRegisterRequest;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.wish.Wish;
import codesquard.support.SupportRepository;

@SpringBootTest
class WishItemServiceTest extends IntegrationTestSupport {

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
		Category category = supportRepository.save(Category.create("식품", "!!"));
		ItemRegisterRequest request = new ItemRegisterRequest(
			"선풍기", 12000L, null, "가양 1동", "판매중", category.getId(), null);

		Member member = supportRepository.save(Member.create("avatar", "pie@pie", "pieeeeeee"));
		Item item1 = supportRepository.save(request.toEntity(member, "thumbnail"));

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
		Category category = supportRepository.save(Category.create("식품", "!!"));
		ItemRegisterRequest request1 = new ItemRegisterRequest(
			"선풍기", 12000L, null, "가양 1동", "판매중", category.getId(), null);
		Member member = supportRepository.save(Member.create("avatar", "pie@pie", "piepie"));
		Item saveItem = supportRepository.save(request1.toEntity(member, "thumbnail"));
		wishItemService.register(saveItem.getId(), member.getId());

		// when
		wishItemService.cancel(saveItem.getId());

		// then
		Item item = em.find(Item.class, saveItem.getId());
		assertThat(item.getWishCount()).isEqualTo(0);
	}

	@Test
	@DisplayName("관심상품 조회에 성공한다.")
	void wishListFindAll() {

		// given
		Category category1 = supportRepository.save(Category.create("가전", "~~~~"));
		Category category2 = supportRepository.save(Category.create("식품", "~~~~!"));
		ItemRegisterRequest request1 = new ItemRegisterRequest(
			"선풍기", 12000L, null, "구래동", "판매중", category1.getId(), null);
		ItemRegisterRequest request2 = new ItemRegisterRequest(
			"전기밥솥", null, null, "화곡동", "판매중", category2.getId(), null);
		ItemRegisterRequest request3 = new ItemRegisterRequest(
			"노트북", null, null, "구래동", "판매중", category1.getId(), null);
		Member member = supportRepository.save(Member.create("avatar", "pie@pie", "piepie"));
		Item item1 = supportRepository.save(request1.toEntity(member, "thumbnail"));
		Item item2 = supportRepository.save(request2.toEntity(member, "thumb"));
		Item item3 = supportRepository.save(request3.toEntity(member, "nail"));
		wishItemService.register(item1.getId(), member.getId());
		wishItemService.register(item2.getId(), member.getId());
		wishItemService.register(item3.getId(), member.getId());

		// when
		List<Wish> all = wishItemService.findAll(null, 10, null);

		// then
		assertThat(all.size()).isEqualTo(3);

	}

	@Test
	@DisplayName("카테고리별 관심상품 목록 조회에 성공한다.")
	void wishListByCategoryTest() {

		// given
		Category category1 = supportRepository.save(Category.create("가전", "~~~~"));
		Category category2 = supportRepository.save(Category.create("식품", "~~~~!"));

		ItemRegisterRequest request1 = new ItemRegisterRequest(
			"선풍기", 12000L, null, "구래동", "판매중", category1.getId(), null);
		ItemRegisterRequest request2 = new ItemRegisterRequest(
			"전기밥솥", null, null, "화곡동", "판매중", category2.getId(), null);
		ItemRegisterRequest request3 = new ItemRegisterRequest(
			"노트북", null, null, "구래동", "판매중", category1.getId(), null);
		Member member = supportRepository.save(Member.create("avatar", "pie@pie", "piepie"));
		Item item1 = supportRepository.save(request1.toEntity(member, "thumbnail"));
		Item item2 = supportRepository.save(request2.toEntity(member, "thumb"));
		Item item3 = supportRepository.save(request3.toEntity(member, "nail"));
		wishItemService.register(item1.getId(), member.getId());
		wishItemService.register(item2.getId(), member.getId());
		wishItemService.register(item3.getId(), member.getId());

		// when
		List<Wish> wishList = wishItemService.findAll(category1.getId(), 10, null);

		// then
		assertThat(wishList.size()).isEqualTo(2);

	}
}
