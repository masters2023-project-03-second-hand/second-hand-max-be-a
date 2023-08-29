package codesquard.app.domain.oauth.support;

import java.util.Optional;

import io.jsonwebtoken.Claims;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Principal {

	private Long memberId;

	public static Principal from(Claims claims) {
		final PrincipalBuilder principal = Principal.builder();
		Optional.ofNullable(claims.get("memberId"))
			.ifPresent(memberId -> principal.memberId(Long.valueOf(memberId.toString())));
		return principal.build();
	}
}
