package codesquard.app;

import java.time.LocalDateTime;

import codesquard.app.domain.jwt.JwtProvider;
import codesquard.app.domain.member.Member;

public class MemberTestSupport {

	public static Member createMember(String avatarUrl, String email, String loginId) {
		return new Member(avatarUrl, email, loginId);
	}

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
