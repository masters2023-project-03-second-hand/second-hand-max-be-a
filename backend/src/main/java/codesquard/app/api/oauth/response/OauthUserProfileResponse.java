package codesquard.app.api.oauth.response;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class OauthUserProfileResponse {
	private final String email;

	private OauthUserProfileResponse(String email) {
		this.email = email;
	}

	public static OauthUserProfileResponse create(String email) {
		return new OauthUserProfileResponse(email);
	}
}
