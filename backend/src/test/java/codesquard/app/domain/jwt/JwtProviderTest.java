package codesquard.app.domain.jwt;

import static codesquard.app.MemberTestSupport.*;

import java.time.LocalDateTime;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import codesquard.app.domain.member.Member;
import codesquard.app.domain.member.MemberRepository;

@ActiveProfiles("test")
@SpringBootTest
class JwtProviderTest {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private JwtProperties jwtProperties;

	@DisplayName("해시맵을 기반으로 JWT 객체를 생성한다")
	@Test
	void createJwtBasedOnAuthenticateMember() {
		// given
		JwtProvider jwtProvider = new JwtProvider(jwtProperties);
		Member member = createMember("avatarUrlValue", "23Yong@gmail.com", "23Yong");
		Member saveMember = memberRepository.save(member);
		LocalDateTime now = createNow();

		// when
		Jwt jwt = jwtProvider.createJwtBasedOnMember(saveMember, now);

		// then
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(jwt.getAccessToken()).isEqualTo(
				createExpectedAccessTokenBy(jwtProvider, member, now));
			softAssertions.assertThat(jwt.getRefreshToken()).isEqualTo(
				createExpectedRefreshTokenBy(jwtProvider, member, now));
			softAssertions.assertAll();
		});
	}

}
