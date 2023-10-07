package codesquard.app.domain.jwt;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import codesquard.app.api.errors.errorcode.JwtErrorCode;
import codesquard.app.api.errors.exception.BadRequestException;
import codesquard.app.api.errors.exception.ForBiddenException;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.oauth.support.Principal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtProvider {

	private final JwtProperties jwtProperties;

	public JwtProvider(JwtProperties jwtProperties) {
		this.jwtProperties = jwtProperties;
	}

	public Jwt createJwtWithRefreshTokenBasedOnMember(Member member, String refreshToken, LocalDateTime now) {
		Map<String, Object> claims = member.createClaims();
		Date expireDateAccessToken = jwtProperties.createExpireAccessTokenDate(now);
		Date expireDateRefreshToken = getClaims(refreshToken).getExpiration();
		return createJwt(claims, expireDateAccessToken, expireDateRefreshToken);
	}

	public Jwt createJwtBasedOnMember(Member member, LocalDateTime now) {
		Map<String, Object> claims = member.createClaims();
		Date expireDateAccessToken = jwtProperties.createExpireAccessTokenDate(now);
		Date expireDateRefreshToken = jwtProperties.getExpireDateRefreshToken(now);
		return createJwt(claims, expireDateAccessToken, expireDateRefreshToken);
	}

	private Jwt createJwt(Map<String, Object> claims, Date expireDateAccessToken, Date expireDateRefreshToken) {
		String accessToken = createToken(claims, expireDateAccessToken);
		String refreshToken = createToken(new HashMap<>(), expireDateRefreshToken);
		return new Jwt(accessToken, refreshToken, expireDateAccessToken, expireDateRefreshToken);
	}

	private String createToken(Map<String, Object> claims, Date expireDate) {
		return Jwts.builder()
			.setClaims(claims)
			.setExpiration(expireDate)
			.signWith(jwtProperties.getKey(), SignatureAlgorithm.HS256)
			.compact();
	}

	public Claims getClaims(String token) {
		// token을 비밀키로 복호화하여 Claims 추출
		try {
			return Jwts.parserBuilder()
				.setSigningKey(jwtProperties.getKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
		} catch (ExpiredJwtException e) {
			throw new ForBiddenException(JwtErrorCode.EXPIRE_TOKEN);
		} catch (JwtException e) {
			throw new BadRequestException(JwtErrorCode.INVALID_TOKEN);
		}
	}

	public void validateToken(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(jwtProperties.getKey())
				.build()
				.parseClaimsJws(token);
		} catch (ExpiredJwtException e) {
			log.error("토큰 만료 에러 : {}", e.getMessage());
			throw new ForBiddenException(JwtErrorCode.EXPIRE_TOKEN);
		} catch (JwtException e) {
			log.error("Jwt 에러 : {}", e.getMessage());
			throw new BadRequestException(JwtErrorCode.INVALID_TOKEN);
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
