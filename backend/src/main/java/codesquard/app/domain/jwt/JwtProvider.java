package codesquard.app.domain.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import codesquard.app.api.errors.errorcode.JwtTokenErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.oauth.support.Principal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtProvider {

	private static final Logger log = LoggerFactory.getLogger(JwtProvider.class);

	private final JwtProperties jwtProperties;

	public JwtProvider(JwtProperties jwtProperties) {
		this.jwtProperties = jwtProperties;
	}

	public Jwt createJwtBasedOnMember(Member member) {
		Map<String, Object> claims = member.createClaims();
		Date expireDateAccessToken = jwtProperties.getExpireDateAccessToken();
		Date expireDateRefreshToken = jwtProperties.getExpireDateRefreshToken();
		return createJwt(claims, expireDateAccessToken, expireDateRefreshToken);
	}

	private Jwt createJwt(Map<String, Object> claims, Date expireDateAccessToken, Date expireDateRefreshToken) {
		// 1. accessToken 생성
		String accessToken = createToken(claims, expireDateAccessToken);

		// 2. refreshToken 생성
		String refreshToken = createToken(new HashMap<>(), expireDateRefreshToken);

		// 3. JWT 생성
		return Jwt.create(accessToken, refreshToken, expireDateAccessToken, expireDateRefreshToken);
	}

	private String createToken(Map<String, Object> claims, Date expireDate) {
		// claims를 비밀키로 이용하여 암호화
		return Jwts.builder()
			.setClaims(claims)
			.setExpiration(expireDate)
			.signWith(jwtProperties.getKey(), SignatureAlgorithm.HS256)
			.compact();
	}

	/**
	 * 액세스 토큰에 해당하는 해시맵 응답
	 * @UnsupportedJwtException – claimsJws 인수가 Claims JWS를 나타내지 않는 경우
	 * @MalformedJwtException – claimsJws 문자열이 유효한 JWS가 아닌 경우
	 * @SignatureException – claimsJws의 JWS 서명이 유효성 검사가 실패하는 경우
	 * @ExpiredJwtException – 만약 명세된 JWT가 Claims JWT이고 Claims이 만료된 경우
	 * @IllegalArgumentException – ClaimsJws 문자열이 null, empty, 공백인 경우
	 * @return token을 비밀키로 복호화한 Claims
	 */
	public Claims getClaims(String token) {
		// token을 비밀키로 복호화하여 Claims 추출
		return Jwts.parserBuilder()
			.setSigningKey(jwtProperties.getKey())
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	public void validateToken(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(jwtProperties.getKey())
				.build()
				.parseClaimsJws(token);
		} catch (ExpiredJwtException e) {
			throw new RestApiException(JwtTokenErrorCode.EXPIRE_TOKEN);
		} catch (JwtException e) {
			throw new RestApiException(JwtTokenErrorCode.INVALID_TOKEN);
		}
	}

	public Principal extractPrincipal(String token) {
		Claims claims = Jwts.parserBuilder()
			.setSigningKey(jwtProperties.getKey())
			.build()
			.parseClaimsJws(token)
			.getBody();

		log.debug("claims : {}", claims);

		return Principal.from(claims, token);
	}
}
