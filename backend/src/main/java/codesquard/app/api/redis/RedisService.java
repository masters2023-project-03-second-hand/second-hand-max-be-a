package codesquard.app.api.redis;

import java.time.Duration;
import java.util.Iterator;
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
public class RedisService {

	private static final String ITEM_ID_PREFIX = "itemId: ";
	private static final Pattern ITEM_ID_PATTERN = Pattern.compile("itemId:*");

	private final RedisTemplate<String, Object> redisTemplate;
	private final ItemRepository itemRepository;

	public String get(String key) {
		return (String)redisTemplate.opsForValue().get(key);
	}

	public void addViewCount(Long itemId) {
		String key = ITEM_ID_PREFIX + itemId;
		ValueOperations<String, Object> value = redisTemplate.opsForValue();
		if (value.get(key) != null) {
			value.increment(key);
			return;
		}
		value.set(key, "1", Duration.ofMinutes(1));
	}

	@Transactional
	@Scheduled(cron = "0 0/1 * * * ?")
	public void deleteViewCountCache() {
		Set<String> keys = redisTemplate.keys(ITEM_ID_PATTERN.pattern());
		Iterator<String> iterator = keys.iterator();
		while (iterator.hasNext()) {
			String data = iterator.next();
			Long itemId = Long.parseLong(data.split(" ")[1]);
			Long viewCount = Long.parseLong((String)redisTemplate.opsForValue().get(data));
			Long originViewCount = itemRepository.findViewCountById(itemId);
			itemRepository.addViewCountFromRedis(itemId, originViewCount + viewCount);
			redisTemplate.delete(data);
			redisTemplate.delete("itemId: " + itemId);
		}
	}
}
