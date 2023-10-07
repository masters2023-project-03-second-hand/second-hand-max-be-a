package codesquard.app.api.wishitem;

import static codesquard.app.CategoryTestSupport.*;
import static codesquard.app.ItemTestSupport.*;
import static codesquard.app.MemberTestSupport.*;
import static codesquard.app.MemberTownTestSupport.*;
import static codesquard.app.RegionTestSupport.*;
import static codesquard.app.domain.item.ItemStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import codesquard.app.api.item.request.ItemRegisterRequest;
import codesquard.app.api.item.response.ItemResponse;
import codesquard.app.api.item.response.ItemResponses;
import codesquard.app.api.wishitem.response.WishCategoryListResponse;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.category.CategoryRepository;
import codesquard.app.domain.image.ImageRepository;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.item.ItemRepository;
import codesquard.app.domain.item.ItemStatus;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.member.MemberRepository;
import codesquard.app.domain.membertown.MemberTownRepository;
import codesquard.app.domain.oauth.support.Principal;
import codesquard.app.domain.region.Region;
import codesquard.app.domain.region.RegionRepository;
import codesquard.app.domain.wish.Wish;
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
	private CategoryRepository categoryRepository;
	@Autowired
	private MemberTownRepository memberTownRepository;
	@Autowired
	private RegionRepository regionRepository;
	@Autowired
	private ImageRepository imageRepository;
	@Autowired
	private EntityManager em;

	@AfterEach
	void tearDown() {
		wishRepository.deleteAllInBatch();
		imageRepository.deleteAllInBatch();
		itemRepository.deleteAllInBatch();
		categoryRepository.deleteAllInBatch();
		memberTownRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
		regionRepository.deleteAllInBatch();
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
		ItemResponses responses = wishItemService.findAll(categoryId, 2, cursor, Principal.from(member));

		// then
		List<ItemResponse> contents = responses.getContents();
		assertAll(
			() -> assertThat(contents).hasSize(2),
			() -> assertThat(Objects.requireNonNull(contents).get(0).getTitle()).isEqualTo("노트북"),
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
		ItemResponses responses = wishItemService.findAll(category1.getId(), 10, null, Principal.from(member));

		// then
		assertThat(responses.getContents()).hasSize(2);
	}

	@DisplayName("한 회원이 등록한 관심 상품들의 중복되지 않은 카테고리를 조회한다")
	@Test
	void readWishCategories() {
		// given
		List<Category> categories = categoryRepository.saveAll(List.of(findByName("스포츠/레저"), findByName("가구/인테리어")));
		Category sport = categories.get(0);
		Category furniture = categories.get(1);
		List<Member> members = memberRepository.saveAll(List.of(
			createMember("avatarUrlValue", "23Yong@gmail.com", "23Yong"),
			createMember("avatarUrlValue", "bruni@gmail.com", "bruni")));
		List<Region> regions = regionRepository.saveAll(
			List.of(createRegion("서울 송파구 가락동"), createRegion("서울 종로구 청운동")));

		Member seller = members.get(0);
		Member buyer = members.get(1);
		memberTownRepository.saveAll(List.of(
			createMemberTown(seller, regions.get(0), true),
			createMemberTown(seller, regions.get(1), false),
			createMemberTown(buyer, regions.get(0), true),
			createMemberTown(buyer, regions.get(0), true)));
		Item item1 = createItem("빈티지 롤러 블레이드", "어린시절 추억의향수를 불러 일으키는 롤러 스케이트입니다.", 200000L, ON_SALE,
			"가락동", "thumbnailUrl", seller, sport);
		Item item2 = createItem("빈티지 의자", "의자 팝니다.", 80000L, ON_SALE,
			"가락동", "thumnailUrl", seller, furniture);
		itemRepository.saveAll(List.of(item1, item2));
		wishRepository.saveAll(List.of(new Wish(buyer, item1), new Wish(buyer, item2)));
		Principal principal = Principal.from(buyer);

		// when
		WishCategoryListResponse response = wishItemService.readWishCategories(principal);

		// then
		assertThat(response)
			.extracting("categories").asList()
			.extracting("categoryId", "categoryName")
			.containsExactlyInAnyOrder(Tuple.tuple(sport.getId(), sport.getName()),
				Tuple.tuple(furniture.getId(), furniture.getName()));

	}
}
