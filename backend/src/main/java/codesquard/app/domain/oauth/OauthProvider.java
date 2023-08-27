package codesquard.app.domain.oauth;

import lombok.Getter;
import lombok.ToString;

@ToString(exclude = {"clientSecret"})
@Getter
public class OauthProvider {
	private final String clientId;
	private final String clientSecret;
	private final String redirectUri;
	private final String tokenUri;
	private final String userInfoUri;

	public OauthProvider(OauthProperties.UserProperties userProperties,
		OauthProperties.ProviderProperties providerProperties) {
		this.clientId = userProperties.getClientId();
		this.clientSecret = userProperties.getClientSecret();
		this.redirectUri = userProperties.getRedirectUri();
		this.tokenUri = providerProperties.getTokenUri();
		this.userInfoUri = providerProperties.getUserInfoUri();
	}
}
