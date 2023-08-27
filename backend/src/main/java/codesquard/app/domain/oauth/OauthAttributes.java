package codesquard.app.domain.oauth;

import java.util.Arrays;
import java.util.Map;

import codesquard.app.api.oauth.response.OauthUserProfileResponse;

public enum OauthAttributes {
	NAVER("naver") {
		@Override
		public OauthUserProfileResponse of(Map<String, Object> attributes) {
			Map<String, Object> responseMap = (Map<String, Object>)attributes.get("response");
			String socialLoginId = convertToNicknameFrom((String)responseMap.get("email"));
			return new OauthUserProfileResponse(socialLoginId);
		}
	};

	private final String providerName;

	OauthAttributes(String providerName) {
		this.providerName = providerName;
	}

	private static String convertToNicknameFrom(String email) {
		final int LOGIN_ID_INDEX = 0;
		return email.split("@")[LOGIN_ID_INDEX];
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