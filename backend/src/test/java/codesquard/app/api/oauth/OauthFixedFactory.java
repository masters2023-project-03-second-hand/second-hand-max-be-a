package codesquard.app.api.oauth;

import java.time.LocalDateTime;

import codesquard.app.domain.jwt.JwtProvider;
import codesquard.app.domain.member.Member;

public class OauthFixedFactory {

	public static LocalDateTime createNow() {
		return LocalDateTime.now();
	}

	public static String createExpectedAccessTokenBy(JwtProvider jwtProvider, Member member,
		LocalDateTime localDateTime) {
		return jwtProvider.createJwtBasedOnMember(member, localDateTime).getAccessToken();
	}

	public static String createExpectedRefreshTokenBy(JwtProvider jwtProvider, Member member,
		LocalDateTime localDateTime) {
		return jwtProvider.createJwtBasedOnMember(member, localDateTime).getRefreshToken();
	}
}
