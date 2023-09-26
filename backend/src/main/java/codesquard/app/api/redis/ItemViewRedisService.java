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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemViewRedisService {

	private static final String ITEM_ID_PREFIX = "itemId: ";
	private static final Pattern ITEM_ID_PATTERN = Pattern.compile("itemId:*");

	private final RedisTemplate<String, Long> redisTemplate;
	private final ItemRepository itemRepository;

	public Long get(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	public void addViewCount(Long itemId) {
		String key = ITEM_ID_PREFIX + itemId;
		ValueOperations<String, Long> value = redisTemplate.opsForValue();
		if (value.get(key) != null) {
			value.increment(key);
			return;
		}
		value.set(key, 1L, Duration.ofMinutes(1));
	}

	@Transactional
	@Scheduled(cron = "0 0/1 * * * ?")
	public void deleteViewCountCache() {
		Set<String> keys = Optional.ofNullable(redisTemplate.keys(ITEM_ID_PATTERN.pattern()))
			.orElseGet(Collections::emptySet);

		for (String key : keys) {
			Long itemId = Long.parseLong(key.split(" ")[1]);
			Long viewCount = Optional.ofNullable(redisTemplate.opsForValue().get(key)).orElse(0L);
			Long originViewCount = itemRepository.findViewCountById(itemId);
			itemRepository.addViewCountFromRedis(itemId, originViewCount + viewCount);
			redisTemplate.delete(key);
			redisTemplate.delete("itemId: " + itemId);
		}
	}
}
