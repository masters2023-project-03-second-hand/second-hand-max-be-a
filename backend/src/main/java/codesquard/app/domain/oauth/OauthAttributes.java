package codesquard.app.domain.oauth;

import java.util.Arrays;
import java.util.Map;

import codesquard.app.api.oauth.response.OauthUserProfileResponse;

public enum OauthAttributes {
	NAVER("naver") {
		@Override
		public OauthUserProfileResponse of(Map<String, Object> attributes) {
			Map<String, Object> responseMap = (Map<String, Object>)attributes.get("response");
			String socialLoginId = (String)responseMap.get("email");
			return OauthUserProfileResponse.create(socialLoginId);
		}
	};

	private final String providerName;

	OauthAttributes(String providerName) {
		this.providerName = providerName;
	}

	public static OauthUserProfileResponse extract(String providerName, Map<String, Object> attributes) {
		return Arrays.stream(values())
			.filter(provider -> providerName.equals(provider.providerName))
			.findAny()
			.orElseThrow(IllegalArgumentException::new)
			.of(attributes);
	}

	public abstract OauthUserProfileResponse of(Map<String, Object> attributes);
}
