package codesquard.app.domain.oauth.client;

import java.util.Map;

import codesquard.app.api.oauth.response.OauthUserProfileResponse;
import codesquard.app.domain.oauth.properties.OauthProperties;

public class NaverOauthClient extends OauthClient {

	public NaverOauthClient(OauthProperties.Naver naver) {
		super(naver.getClientId(),
			naver.getClientSecret(),
			naver.getTokenUri(),
			naver.getUserInfoUri(),
			naver.getRedirectUri());
	}

	@Override
	public OauthUserProfileResponse createOauthUserProfileResponse(Map<String, Object> attributes) {
		Map<String, Object> responseMap = (Map<String, Object>)attributes.get("response");
		String email = (String)responseMap.get("email");
		return OauthUserProfileResponse.create(email);
	}
}
