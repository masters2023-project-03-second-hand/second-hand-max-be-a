package codesquard.app.api.oauth.response;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class OauthUserProfileResponse {

	private final String email;
	private final String profile_image;

	private OauthUserProfileResponse(String email, String profile_image) {
		this.email = email;
		this.profile_image = profile_image;
	}

	public static OauthUserProfileResponse create(String email, String profile_image) {
		return new OauthUserProfileResponse(email, profile_image);
	}
}
