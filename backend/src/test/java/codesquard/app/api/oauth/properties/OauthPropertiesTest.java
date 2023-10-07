package codesquard.app.api.oauth.properties;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import codesquard.app.domain.oauth.client.OauthClient;
import codesquard.app.domain.oauth.properties.OauthProperties;

@ActiveProfiles("test")
@SpringBootTest
public class OauthPropertiesTest {

	@Autowired
	private OauthProperties oauthProperties;

	@DisplayName("provider가 주어지고 provider에 따른 oauth 설정값들을 OauthProvider가 가진다")
	@Test
	void createOauthClientMap() {
		// given
		String provider = "naver";

		// when
		Map<String, OauthClient> oauthClientMap = oauthProperties.createOauthClientMap();
		// then
		OauthClient naverOauthProvider = oauthClientMap.get(provider);
		assertAll(() -> {
			assertThat(naverOauthProvider)
				.extracting("clientId")
				.isEqualTo("NLiiJeoRUwAoN3VtfjQh");
			assertThat(naverOauthProvider)
				.extracting("redirectUri")
				.isEqualTo("http://localhost:5173/my-account/oauth");
			assertThat(naverOauthProvider)
				.extracting("tokenUri")
				.isEqualTo("https://nid.naver.com/oauth2.0/token");
			assertThat(naverOauthProvider)
				.extracting("userInfoUri")
				.isEqualTo("https://openapi.naver.com/v1/nid/me");
		});
	}
}
