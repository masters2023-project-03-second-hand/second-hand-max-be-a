package codesquard.app.domain.oauth;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import codesquard.app.IntegrationTestSupport;
import codesquard.app.domain.oauth.client.OauthClient;
import codesquard.app.domain.oauth.repository.OauthClientRepository;

public class InMemoryOauthClientRepositoryTest extends IntegrationTestSupport {

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
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(oauthClient)
				.extracting("clientId")
				.isEqualTo("NLiiJeoRUwAoN3VtfjQh");
			softAssertions.assertThat(oauthClient)
				.extracting("redirectUri")
				.isEqualTo("http://localhost:8080/redirect/auth");
			softAssertions.assertThat(oauthClient)
				.extracting("tokenUri")
				.isEqualTo("https://nid.naver.com/oauth2.0/token");
			softAssertions.assertThat(oauthClient)
				.extracting("userInfoUri")
				.isEqualTo("https://openapi.naver.com/v1/nid/me");
			softAssertions.assertAll();
		});
	}
}
