package codesquard.app.api.sales;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import codesquard.app.api.item.request.ItemRegisterRequest;
import codesquard.app.api.item.response.ItemResponses;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.item.ItemRepository;
import codesquard.app.domain.item.ItemStatus;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.member.MemberRepository;
import codesquard.app.domain.sales.SalesStatus;
import codesquard.support.SupportRepository;

@ActiveProfiles("test")
@SpringBootTest
class SalesItemServiceTest {

	@Autowired
	private SalesItemService salesItemService;

	@Autowired
	private SupportRepository supportRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private ItemRepository itemRepository;

	@AfterEach
	void tearDown() {
		itemRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("판매상품목록 전체 조회에 성공한다.")
	void salesListFindAllTest() {

		// given
		fixtureItemAndMember();

		// when
		ItemResponses all = salesItemService.findAll(SalesStatus.All, 10, null);

		// then
		assertAll(
			() -> assertThat(all.getContents()).hasSize(3),
			() -> assertThat(all.getContents().get(0).getTitle()).isEqualTo("노트북"),
			() -> assertThat(all.getContents().get(1).getTitle()).isEqualTo("전기밥솥"),
			() -> assertThat(all.getContents().get(2).getTitle()).isEqualTo("선풍기")
		);
	}

	@Test
	@DisplayName("판매상품목록 <판매중> 조회에 성공한다.")
	void salesListFindOnSalesTest() {

		// given
		fixtureItemAndMember();

		// when
		ItemResponses all = salesItemService.findAll(SalesStatus.ON_SALE, 10, null);

		// then
		assertAll(
			() -> assertThat(all.getContents()).hasSize(2),
			() -> assertThat(all.getContents().get(0).getTitle()).isEqualTo("노트북"),
			() -> assertThat(all.getContents().get(1).getTitle()).isEqualTo("전기밥솥")
		);
	}

	@Test
	@DisplayName("판매상품목록 <판매완료> 조회에 성공한다.")
	void salesListFindSoldOutTest() {

		// given
		fixtureItemAndMember();

		// when
		ItemResponses all = salesItemService.findAll(SalesStatus.SOLD_OUT, 10, null);

		// then
		assertAll(
			() -> assertThat(all.getContents()).hasSize(1),
			() -> assertThat(all.getContents().get(0).getTitle()).isEqualTo("선풍기")
		);
	}

	private void fixtureItemAndMember() {
		Category category1 = supportRepository.save(new Category("가전", "~~~~"));
		ItemRegisterRequest request1 = new ItemRegisterRequest(
			"선풍기", 12000L, null, "구래동", ItemStatus.SOLD_OUT, category1.getId(), null);
		ItemRegisterRequest request2 = new ItemRegisterRequest(
			"전기밥솥", null, null, "구래동", ItemStatus.ON_SALE, category1.getId(), null);
		ItemRegisterRequest request3 = new ItemRegisterRequest(
			"노트북", null, null, "구래동", ItemStatus.RESERVED, category1.getId(), null);
		Member member = supportRepository.save(new Member("avatar", "pie@pie", "piepie"));
		supportRepository.save(request1.toEntity(member, "thumbnail"));
		supportRepository.save(request2.toEntity(member, "thumb"));
		supportRepository.save(request3.toEntity(member, "nail"));
	}
}
