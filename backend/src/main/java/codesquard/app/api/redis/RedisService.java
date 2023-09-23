package codesquard.app.api.redis;

import java.time.Duration;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.logging.log4j.util.Strings;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.api.errors.errorcode.JwtTokenErrorCode;
import codesquard.app.api.errors.errorcode.OauthErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.domain.item.ItemRepository;
import codesquard.app.domain.jwt.Jwt;
import codesquard.app.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisService {

	private static final String LOGOUT = "logout";
	private static final String REFRESH_TOKEN_PREFIX = "RT:";
	private static final Pattern REFRESH_TOKEN_PATTERN = Pattern.compile("RT:*");
	private static final String ITEM_ID_PREFIX = "itemId: ";
	private static final Pattern ITEM_ID_PATTERN = Pattern.compile("itemId:*");

	private final RedisTemplate<String, Object> redisTemplate;
	private final ItemRepository itemRepository;

	public String findEmailByRefreshTokenValue(String refreshToken) {
		Set<String> keys = redisTemplate.keys(REFRESH_TOKEN_PATTERN.pattern());
		if (keys == null) {
			throw new RestApiException(JwtTokenErrorCode.EMPTY_TOKEN);
		}
		return keys.stream()
			.filter(key -> Objects.equals(redisTemplate.opsForValue().get(key), refreshToken))
			.findAny()
			.map(email -> email.replace(REFRESH_TOKEN_PREFIX, Strings.EMPTY))
			.orElseThrow(() -> new RestApiException(JwtTokenErrorCode.INVALID_TOKEN));
	}

	public void banAccessToken(String accessToken, long expiration) {
		redisTemplate.opsForValue()
			.set(accessToken, LOGOUT, expiration, TimeUnit.MILLISECONDS);
	}

	public boolean delete(String key) {
		if (key == null) {
			return false;
		}
		return Boolean.TRUE.equals(redisTemplate.delete(key));
	}

	public void saveRefreshToken(Member member, Jwt jwt) {
		// key: "RT:" + email, value : 리프레쉬 토큰값
		redisTemplate.opsForValue().set(member.createRedisKey(),
			jwt.getRefreshToken(),
			jwt.convertExpireDateRefreshTokenTimeWithLong(),
			TimeUnit.MILLISECONDS);
	}

	public Object get(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	public void validateAlreadyLogout(String token) {
		String logout = (String)redisTemplate.opsForValue().get(token);
		if (LOGOUT.equals(logout)) {
			throw new RestApiException(OauthErrorCode.NOT_LOGIN_STATE);
		}
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
