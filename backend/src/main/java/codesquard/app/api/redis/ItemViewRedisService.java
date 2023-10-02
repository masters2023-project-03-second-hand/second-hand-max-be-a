package codesquard.app.api.redis;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.domain.item.ItemRepository;
import codesquard.app.domain.oauth.support.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemViewRedisService {

	public static final String ITEM_ID_PREFIX = "itemId: ";
	private static final Pattern ITEM_ID_PATTERN = Pattern.compile("itemId:*");

	private final RedisTemplate<String, String> redisTemplate;
	private final ItemRepository itemRepository;

	public String get(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	public void addViewCount(Long itemId, Principal principal) {
		String key = ITEM_ID_PREFIX + itemId;
		String itemViewKey = principal.createItemViewKey(key);
		if (!isFirstItemView(itemViewKey)) {
			return;
		}
		ValueOperations<String, String> value = redisTemplate.opsForValue();
		value.set(itemViewKey, "true", Duration.ofDays(1L));

		if (value.get(key) != null) {
			value.increment(key);
			log.debug("상품 게시글 조회수 증가 결과 : key={}, value={}", key, value.get(key));
			return;
		}

		value.set(key, "1", Duration.ofMinutes(1));
		log.debug("상품 게시글 조회수 증가 결과 : key={}, value={}", key, value.get(key));
	}

	private boolean isFirstItemView(String key) {
		return !Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}

	@Transactional
	@Scheduled(cron = "0 0/1 * * * ?")
	public void deleteViewCountCache() {
		Set<String> keys = Optional.ofNullable(redisTemplate.keys(ITEM_ID_PATTERN.pattern()))
			.orElseGet(Collections::emptySet);

		for (String key : keys) {
			Long itemId = Long.parseLong(key.split(" ")[1]);
			Long viewCount = Long.valueOf(Optional.ofNullable(redisTemplate.opsForValue().get(key)).orElse("0"));
			Long originViewCount = itemRepository.findViewCountById(itemId);
			itemRepository.addViewCountFromRedis(itemId, originViewCount + viewCount);
			redisTemplate.delete(key);
			redisTemplate.delete("itemId: " + itemId);
		}
	}
}
