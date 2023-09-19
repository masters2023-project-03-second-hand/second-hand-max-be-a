package codesquard.app.api.redis;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import codesquard.app.api.errors.errorcode.JwtTokenErrorCode;
import codesquard.app.api.errors.errorcode.OauthErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.domain.jwt.Jwt;
import codesquard.app.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisService {
	private final RedisTemplate<String, Object> redisTemplate;

	public String findEmailByRefreshTokenValue(String refreshToken) {
		Set<String> keys = redisTemplate.keys("RT:*");
		if (keys == null) {
			throw new RestApiException(JwtTokenErrorCode.EMPTY_TOKEN);
		}
		return keys.stream()
			.filter(key -> Objects.equals(redisTemplate.opsForValue().get(key), refreshToken))
			.findAny()
			.map(email -> email.replace("RT:", ""))
			.orElse(null);
	}

	public void banAccessToken(String accessToken, long expiration) {
		redisTemplate.opsForValue()
			.set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
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
		String value = String.valueOf(redisTemplate.opsForValue().get(token));
		if (Objects.equals(value, "logout")) {
			throw new RestApiException(OauthErrorCode.NOT_LOGIN_STATE);
		}
	}
}
