package codesquard.app.api.wishitem;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import codesquard.app.api.item.request.ItemRegisterRequest;
import codesquard.app.api.item.response.ItemResponse;
import codesquard.app.api.item.response.ItemResponses;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.item.ItemRepository;
import codesquard.app.domain.item.ItemStatus;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.member.MemberRepository;
import codesquard.app.domain.wish.WishRepository;
import codesquard.app.domain.wish.WishStatus;
import codesquard.support.SupportRepository;

@ActiveProfiles("test")
@SpringBootTest
class WishItemServiceTest {

	@Autowired
	private WishItemService wishItemService;
	@Autowired
	private WishRepository wishRepository;
	@Autowired
	private SupportRepository supportRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private ItemRepository itemRepository;
	@Autowired
	private EntityManager em;

	@AfterEach
	void tearDown() {
		wishRepository.deleteAllInBatch();
		itemRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("관심상품 등록에 성공한다.")
	void wishRegisterTest() {

		// given
		Category category = supportRepository.save(new Category("식품", "!!"));
		ItemRegisterRequest request = new ItemRegisterRequest(
			"선풍기", 12000L, null, "가양 1동", ItemStatus.ON_SALE, category.getId(), null);

		Member member = supportRepository.save(new Member("avatar", "pie@pie", "pieeeeeee"));
		Item item1 = supportRepository.save(request.toEntity(member, "thumbnail"));

		// when
		wishItemService.changeWishStatus(item1.getId(), member.getId(), WishStatus.YES);

		// then
		Item item = em.find(Item.class, item1.getId());
		assertThat(item.getWishCount()).isEqualTo(1);
	}

	@Test
	@DisplayName("관심상품 취소에 성공한다.")
	void wishCancelTest() {

		// given
		Category category = supportRepository.save(new Category("식품", "!!"));
		ItemRegisterRequest request1 = new ItemRegisterRequest(
			"선풍기", 12000L, null, "가양 1동", ItemStatus.ON_SALE, category.getId(), null);
		Member member = supportRepository.save(new Member("avatar", "pie@pie", "piepie"));
		Item saveItem = supportRepository.save(request1.toEntity(member, "thumbnail"));
		wishItemService.changeWishStatus(saveItem.getId(), member.getId(), WishStatus.YES);

		// when
		wishItemService.changeWishStatus(saveItem.getId(), member.getId(), WishStatus.NO);

		// then
		Item item = em.find(Item.class, saveItem.getId());
		assertThat(item.getWishCount()).isEqualTo(0);
	}

	@Test
	@DisplayName("관심상품 조회에 성공한다.")
	void wishListFindAll() {

		// given
		Category category1 = supportRepository.save(new Category("가전", "~~~~"));
		Category category2 = supportRepository.save(new Category("식품", "~~~~!"));
		ItemRegisterRequest request1 = new ItemRegisterRequest(
			"선풍기", 12000L, null, "구래동", ItemStatus.ON_SALE, category1.getId(), null);
		ItemRegisterRequest request2 = new ItemRegisterRequest(
			"전기밥솥", null, null, "화곡동", ItemStatus.ON_SALE, category2.getId(), null);
		ItemRegisterRequest request3 = new ItemRegisterRequest(
			"노트북", null, null, "구래동", ItemStatus.ON_SALE, category1.getId(), null);
		Member member = supportRepository.save(new Member("avatar", "pie@pie", "piepie"));
		Item item1 = supportRepository.save(request1.toEntity(member, "thumbnail"));
		Item item2 = supportRepository.save(request2.toEntity(member, "thumb"));
		Item item3 = supportRepository.save(request3.toEntity(member, "nail"));
		wishItemService.changeWishStatus(item1.getId(), member.getId(), WishStatus.YES);
		wishItemService.changeWishStatus(item2.getId(), member.getId(), WishStatus.YES);
		wishItemService.changeWishStatus(item3.getId(), member.getId(), WishStatus.YES);

		// when
		Long categoryId = null;
		Long cursor = null;
		ItemResponses responses = wishItemService.findAll(categoryId, 2, cursor);

		// then
		List<ItemResponse> contents = responses.getContents();
		assertAll(
			() -> assertThat(contents).hasSize(2),
			() -> assertThat(contents.get(0).getTitle()).isEqualTo("노트북"),
			() -> assertThat(responses.getPaging().isHasNext()).isTrue(),
			() -> assertThat(responses.getPaging().getNextCursor()).isEqualTo(item2.getId())
		);
	}

	@Test
	@DisplayName("카테고리별 관심상품 목록 조회에 성공한다.")
	void wishListByCategoryTest() {

		// given
		Category category1 = supportRepository.save(new Category("가전", "~~~~"));
		Category category2 = supportRepository.save(new Category("식품", "~~~~!"));

		ItemRegisterRequest request1 = new ItemRegisterRequest(
			"선풍기", 12000L, null, "구래동", ItemStatus.ON_SALE, category1.getId(), null);
		ItemRegisterRequest request2 = new ItemRegisterRequest(
			"전기밥솥", null, null, "화곡동", ItemStatus.ON_SALE, category2.getId(), null);
		ItemRegisterRequest request3 = new ItemRegisterRequest(
			"노트북", null, null, "구래동", ItemStatus.ON_SALE, category1.getId(), null);
		Member member = supportRepository.save(new Member("avatar", "pie@pie", "piepie"));
		Item item1 = supportRepository.save(request1.toEntity(member, "thumbnail"));
		Item item2 = supportRepository.save(request2.toEntity(member, "thumb"));
		Item item3 = supportRepository.save(request3.toEntity(member, "nail"));
		wishItemService.changeWishStatus(item1.getId(), member.getId(), WishStatus.YES);
		wishItemService.changeWishStatus(item2.getId(), member.getId(), WishStatus.YES);
		wishItemService.changeWishStatus(item3.getId(), member.getId(), WishStatus.YES);

		// when
		ItemResponses responses = wishItemService.findAll(category1.getId(), 10, null);

		// then
		assertThat(responses.getContents()).hasSize(2);
	}
}
