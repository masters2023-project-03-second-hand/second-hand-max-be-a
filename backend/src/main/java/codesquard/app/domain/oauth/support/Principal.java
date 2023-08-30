package codesquard.app.domain.oauth.support;

import java.util.Optional;

import codesquard.app.domain.member.Member;
import io.jsonwebtoken.Claims;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Principal {

	private Long memberId;
	private String email;
	private String loginId;
	private Long expireDateAccessToken;
	private String accessToken;

	@Builder
	public Principal(Long memberId, String email, String loginId, Long expireDateAccessToken, String accessToken) {
		this.memberId = memberId;
		this.email = email;
		this.loginId = loginId;
		this.expireDateAccessToken = expireDateAccessToken;
		this.accessToken = accessToken;
	}

	public static Principal from(Claims claims, String accessToken) {
		PrincipalBuilder principal = Principal.builder();
		Optional.ofNullable(claims.get("memberId"))
			.ifPresent(memberId -> principal.memberId(Long.valueOf(memberId.toString())));
		Optional.ofNullable(claims.get("email"))
			.ifPresent(email -> principal.email((String)email));
		Optional.ofNullable(claims.get("loginId"))
			.ifPresent(loginId -> principal.loginId((String)loginId));
		Optional.ofNullable(claims.get("exp"))
			.ifPresent(expireDateAccessToken -> principal.expireDateAccessToken(
				Long.parseLong(expireDateAccessToken.toString())));
		Optional.ofNullable(accessToken)
			.ifPresent(token -> principal.accessToken(accessToken));
		return principal.build();
	}

	public static Principal from(Member member) {
		return Principal.builder()
			.memberId(member.getId())
			.email(member.getEmail())
			.build();
	}

	public String createRedisKey() {
		return "RT:" + email;
	}
}
