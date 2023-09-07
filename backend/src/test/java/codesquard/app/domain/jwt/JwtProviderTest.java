package codesquard.app.domain.jwt;

import java.time.LocalDateTime;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import codesquard.app.IntegrationTestSupport;
import codesquard.app.api.oauth.OauthFixedFactory;
import codesquard.app.domain.member.Member;

class JwtProviderTest extends IntegrationTestSupport {

	@Autowired
	private JwtProperties jwtProperties;

	@DisplayName("해시맵을 기반으로 JWT 객체를 생성한다")
	@Test
	public void createJwtBasedOnAuthenticateMember() {
		// given
		JwtProvider jwtProvider = new JwtProvider(jwtProperties);
		Member member = OauthFixedFactory.createFixedMember();
		Member saveMember = memberRepository.save(member);

		LocalDateTime now = OauthFixedFactory.createNow();

		// when
		Jwt jwt = jwtProvider.createJwtBasedOnMember(saveMember, now);

		// then
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(jwt.getAccessToken()).isEqualTo(
				OauthFixedFactory.createExpectedAccessTokenBy(jwtProvider, member, now));
			softAssertions.assertThat(jwt.getRefreshToken()).isEqualTo(
				OauthFixedFactory.createExpectedRefreshTokenBy(jwtProvider, member, now));
			softAssertions.assertAll();
		});
	}

}
