package codesquard.app.api.oauth.properties;

import java.util.Map;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import codesquard.app.IntegrationTestSupport;
import codesquard.app.domain.oauth.client.OauthClient;
import codesquard.app.domain.oauth.properties.OauthProperties;

public class OauthPropertiesTest extends IntegrationTestSupport {

	@Autowired
	private OauthProperties oauthProperties;

	@DisplayName("provider가 주어지고 provider에 따른 oauth 설정값들을 OauthProvider가 가진다")
	@Test
	public void createOauthClientMap() {
		// given
		String provider = "naver";

		// when
		Map<String, OauthClient> oauthClientMap = oauthProperties.createOauthClientMap();
		OauthClient naverOauthProvider = oauthClientMap.get(provider);

		// then
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(naverOauthProvider)
				.extracting("clientId")
				.isEqualTo("NLiiJeoRUwAoN3VtfjQh");
			softAssertions.assertThat(naverOauthProvider)
				.extracting("redirectUri")
				.isEqualTo("http://localhost:8080/redirect/auth");
			softAssertions.assertThat(naverOauthProvider)
				.extracting("tokenUri")
				.isEqualTo("https://nid.naver.com/oauth2.0/token");
			softAssertions.assertThat(naverOauthProvider)
				.extracting("userInfoUri")
				.isEqualTo("https://openapi.naver.com/v1/nid/me");
			softAssertions.assertAll();
		});
	}
}
