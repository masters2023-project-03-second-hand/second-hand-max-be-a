package codesquard.app.domain.oauth;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.Getter;
import lombok.ToString;

@ToString
@ConfigurationProperties(prefix = "oauth2")
public class OauthProperties {
	private final Map<String, UserProperties> user;
	private final Map<String, ProviderProperties> provider;

	@ConstructorBinding
	public OauthProperties(Map<String, UserProperties> user, Map<String, ProviderProperties> provider) {
		this.user = user;
		this.provider = provider;
	}

	public Map<String, OauthProvider> createOauthProviderMap() {
		Map<String, OauthProvider> oauthProviderMap = new HashMap<>();
		Set<String> providerNames = user.keySet();
		for (String providerName : providerNames) {
			UserProperties userProperties = user.get(providerName);
			ProviderProperties providerProperties = provider.get(providerName);
			oauthProviderMap.put(providerName, new OauthProvider(userProperties, providerProperties));
		}
		return oauthProviderMap;
	}

	@ToString
	@Getter
	public static class UserProperties {
		private final String clientId;
		private final String clientSecret;
		private final String redirectUri;

		@ConstructorBinding
		public UserProperties(String clientId, String clientSecret, String redirectUri) {
			this.clientId = clientId;
			this.clientSecret = clientSecret;
			this.redirectUri = redirectUri;
		}
	}

	@ToString
	@Getter
	public static class ProviderProperties {
		private final String tokenUri;
		private final String userInfoUri;

		@ConstructorBinding
		public ProviderProperties(String tokenUri, String userInfoUri) {
			this.tokenUri = tokenUri;
			this.userInfoUri = userInfoUri;
		}
	}
}
