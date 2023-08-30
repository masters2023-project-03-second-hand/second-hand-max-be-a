package codesquard.app.domain.jwt;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Component
public class JwtProperties {

	private final Key key;
	private final Long accessTokenExpirationMillisecond;
	private final Long refreshTokenExpirationMillisecond;

	public JwtProperties(@Value("${jwt.secret-key}") String jwtSecret,
		@Value("${jwt.accesstoken-expiration-milliseconds}") Long accessTokenExpirationMillisecond,
		@Value("${jwt.refreshtoken-expiration-milliseconds}") Long refreshTokenExpirationMillisecond) {
		byte[] secret = jwtSecret.getBytes();
		key = Keys.hmacShaKeyFor(secret);
		this.accessTokenExpirationMillisecond = accessTokenExpirationMillisecond;
		this.refreshTokenExpirationMillisecond = refreshTokenExpirationMillisecond;
	}

	public Date getExpireDateAccessToken() {
		return new Date(System.currentTimeMillis() + accessTokenExpirationMillisecond);
	}

	public Date getExpireDateRefreshToken() {
		return new Date(System.currentTimeMillis() + refreshTokenExpirationMillisecond);
	}
}
