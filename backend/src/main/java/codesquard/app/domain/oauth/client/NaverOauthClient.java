package codesquard.app.domain.oauth.client;

import codesquard.app.domain.oauth.properties.OauthProperties;

public class NaverOauthClient extends OauthClient {

	public NaverOauthClient(OauthProperties.Naver naver) {
		super(naver.getClientId(),
			naver.getClientSecret(),
			naver.getTokenUri(),
			naver.getUserInfoUri(),
			naver.getRedirectUri());
	}
}
