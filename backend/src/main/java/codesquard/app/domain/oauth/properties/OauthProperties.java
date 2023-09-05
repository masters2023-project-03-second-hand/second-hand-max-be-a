package codesquard.app.domain.oauth.properties;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import codesquard.app.domain.oauth.client.NaverOauthClient;
import codesquard.app.domain.oauth.client.OauthClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@ConfigurationProperties(prefix = "oauth2")
public class OauthProperties {

	private final Naver naver;

	@ConstructorBinding
	public OauthProperties(Naver naver) {
		this.naver = naver;
	}

	public Map<String, OauthClient> createOauthClientMap() {
		Map<String, OauthClient> oauthClientMap = new HashMap<>();
		oauthClientMap.put("naver", new NaverOauthClient(naver));
		return oauthClientMap;
	}

	@Getter
	@RequiredArgsConstructor
	public static class Naver {

		private final String clientId;
		private final String clientSecret;
		private final String tokenUri;
		private final String userInfoUri;
		private final String redirectUri;
	}
}
