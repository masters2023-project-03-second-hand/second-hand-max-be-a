package codesquard.app.api.redis;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import codesquard.app.CacheTestSupport;
import codesquard.app.api.item.request.ItemRegisterRequest;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.item.ItemRepository;
import codesquard.app.domain.item.ItemStatus;
import codesquard.app.domain.member.Member;
import codesquard.support.SupportRepository;

@ActiveProfiles("test")
class ItemViewRedisServiceTest extends CacheTestSupport {

	@SpyBean
	private ItemViewRedisService itemViewRedisService;

	@Autowired
	private SupportRepository supportRepository;

	@Autowired
	private ItemRepository itemRepository;

	@AfterEach
	void tearDown() {
		itemRepository.deleteAllInBatch();
	}

	@Disabled
	@DisplayName("상품 상세 조회 시 viewCount가 증가한다.")
	@Test
	void viewCountTest() {
		// given
		Category category = supportRepository.save(new Category("식품", "~~~~"));
		ItemRegisterRequest request1 = new ItemRegisterRequest(
			"선풍기", 12000L, null, "가양 1동", ItemStatus.ON_SALE, category.getId(), null);
		Member member = supportRepository.save(new Member("avatar", "pie@pie", "pieeeeeee"));
		Item item = supportRepository.save(request1.toEntity(member, "thumbnail"));
		itemViewRedisService.addViewCount(item.getId());
		itemViewRedisService.addViewCount(item.getId());

		// when
		await().atMost(2, TimeUnit.MINUTES).untilAsserted(
			() -> {
				verify(itemViewRedisService, atLeast(2
				)).deleteViewCountCache();
			}
		);

		// then
		Optional<Item> saveItem = itemRepository.findById(item.getId());
		assertThat(saveItem.get().getViewCount()).isEqualTo(2);
	}

	@DisplayName("회원이 두번째로 상품 상세 조회시 뷰 카운트는 증가하지 않는다")
	@Test
	public void addViewCount() {
		// given

		// when
		itemViewRedisService.addViewCount(1L);
		itemViewRedisService.addViewCount(1L);
		// then
		Long viewCount = itemViewRedisService.get("itemId: 1");
		assertThat(viewCount).isEqualTo(1L);
	}
}
