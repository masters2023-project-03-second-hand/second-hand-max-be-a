package codesquard.app.api.redis;

import static codesquard.app.api.redis.ItemViewRedisService.*;
import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import codesquard.app.CacheTestSupport;
import codesquard.app.MemberTestSupport;
import codesquard.app.api.item.request.ItemRegisterRequest;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.category.CategoryRepository;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.item.ItemRepository;
import codesquard.app.domain.item.ItemStatus;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.oauth.support.Principal;
import codesquard.support.SupportRepository;

@ActiveProfiles("test")
class ItemViewRedisServiceTest extends CacheTestSupport {

	@SpyBean
	private ItemViewRedisService itemViewRedisService;

	@Autowired
	private SupportRepository supportRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@AfterEach
	void tearDown() {
		itemRepository.deleteAllInBatch();
		categoryRepository.deleteAllInBatch();
		Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().flushAll();
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
		Principal principal = Principal.from(member);
		Item item = supportRepository.save(request1.toEntity(member, "thumbnail"));
		itemViewRedisService.addViewCount(item.getId(), principal);
		itemViewRedisService.addViewCount(item.getId(), principal);

		// when
		await().atMost(2, TimeUnit.MINUTES).untilAsserted(
			() -> {
				verify(itemViewRedisService, atLeast(2
				)).deleteViewCountCache();
			}
		);

		// then
		Item saveItem = itemRepository.findById(item.getId()).orElseThrow();
		assertThat(saveItem.getViewCount()).isEqualTo(2);
	}

	@DisplayName("회원이 두번째로 상품 상세 조회시 뷰 카운트는 증가하지 않는다")
	@Test
	void addViewCount() {
		// given
		Member member = MemberTestSupport.createMember("avatarUrl", "23Yong@gmail.com", "23Yong");
		Principal principal = Principal.from(member);
		Long itemId = 1L;
		// when
		itemViewRedisService.addViewCount(itemId, principal);
		itemViewRedisService.addViewCount(itemId, principal);
		// then
		String s = itemViewRedisService.get(ITEM_ID_PREFIX + itemId);
		Long viewCount = Long.valueOf(s);
		String itemViewValue = itemViewRedisService.get(principal.createItemViewKey(ITEM_ID_PREFIX + itemId));
		assertAll(
			() -> assertThat(viewCount).isEqualTo(1L),
			() -> assertThat(itemViewValue).isEqualTo("true")
		);

	}
}
