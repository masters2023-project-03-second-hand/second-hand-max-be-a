package codesquard.app.domain.oauth;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import codesquard.app.domain.oauth.client.OauthClient;
import codesquard.app.domain.oauth.repository.OauthClientRepository;

@ActiveProfiles("test")
@SpringBootTest
class InMemoryOauthClientRepositoryTest {

	@Autowired
	private OauthClientRepository oauthClientRepository;

	@DisplayName("provider가 주어지고 provider에 따른 OauthProvider를 조회하고 oauth 설정들을 가진다")
	@Test
	public void findByProviderName() {
		// given
		String provider = "naver";

		// when
		OauthClient oauthClient = oauthClientRepository.findOneBy(provider);

		// then
		assertAll(() -> {
			assertThat(oauthClient)
				.extracting("clientId")
				.isEqualTo("NLiiJeoRUwAoN3VtfjQh");
			assertThat(oauthClient)
				.extracting("redirectUri")
				.isEqualTo("http://localhost:5173/my-account/oauth");
			assertThat(oauthClient)
				.extracting("tokenUri")
				.isEqualTo("https://nid.naver.com/oauth2.0/token");
			assertThat(oauthClient)
				.extracting("userInfoUri")
				.isEqualTo("https://openapi.naver.com/v1/nid/me");
		});
	}
}
