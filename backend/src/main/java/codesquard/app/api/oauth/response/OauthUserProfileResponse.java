package codesquard.app.api.oauth.response;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class OauthUserProfileResponse {
	private final String socialLoginId;

	private OauthUserProfileResponse(String socialLoginId) {
		this.socialLoginId = socialLoginId;
	}

	public static OauthUserProfileResponse create(String socialLoginId) {
		return new OauthUserProfileResponse(socialLoginId);
	}
}
