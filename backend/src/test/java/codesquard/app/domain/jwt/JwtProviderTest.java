package codesquard.app.domain.jwt;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import codesquard.app.IntegrationTestSupport;
import codesquard.app.domain.member.Member;
import io.jsonwebtoken.security.Keys;

class JwtProviderTest extends IntegrationTestSupport {

	private static final Logger log = LoggerFactory.getLogger(JwtProviderTest.class);

	@Autowired
	private JwtProvider jwtProvider;

	@MockBean
	private JwtProperties jwtProperties;

	@DisplayName("해시맵을 기반으로 JWT 객체를 생성한다")
	@Test
	public void createJwtBasedOnAuthenticateMember() {
		// given
		Member member = Member.create("avatarUrl", "23Yong1234@gmail.com", "23Yong");
		Member saveMember = memberRepository.save(member);

		long now = LocalDateTime.of(2023, 8, 29, 0, 0).toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli();
		Date expireDateAccessToken = new Date(
			now + jwtProperties.getAccessTokenExpirationMillisecond());
		Date expireDateRefreshToken = new Date(
			now + jwtProperties.getRefreshTokenExpirationMillisecond());

		String jwtSecret = "jwtSecretjwtSecretjwtSecretjwtSecret";
		byte[] secret = jwtSecret.getBytes();
		Key key = Keys.hmacShaKeyFor(secret);

		// mocking
		Mockito.when(jwtProperties.getKey()).thenReturn(key);
		Mockito.when(jwtProperties.getExpireDateAccessToken()).thenReturn(expireDateAccessToken);
		Mockito.when(jwtProperties.getExpireDateRefreshToken()).thenReturn(expireDateRefreshToken);

		// when
		Jwt jwt = jwtProvider.createJwtBasedOnMember(saveMember);

		// then
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(jwt.getAccessToken()).isEqualTo(
				"eyJhbGciOiJIUzI1NiJ9.eyJsb2dpbklkIjoiMjNZb25nIiwiZW1haWwiOiIyM1lvbmcxMjM0QGdtYWlsLmNvbSIsIm1lbWJlcklkIjoxLCJleHAiOjE2OTMyNjcyMDB9.353ki9Z28GzqNIrMcEagamRGhQSrbcpOQbrkh0lDfaE");
			softAssertions.assertThat(jwt.getRefreshToken()).isEqualTo(
				"eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2OTMyNjcyMDB9.Lfukp8yOzpVtkPtKhezt54CuqbRuSe8zyLqF_14u8Hw");
			softAssertions.assertAll();
		});
	}

}
