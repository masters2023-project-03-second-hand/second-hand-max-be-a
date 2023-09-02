package codesquard.app.api.oauth.response;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class OauthUserProfileResponse {

	private final String email;
	private final String profileImage;

	private OauthUserProfileResponse(String email, String profileImage) {
		this.email = email;
		this.profileImage = profileImage;
	}

	public static OauthUserProfileResponse create(String email, String profileImage) {
		return new OauthUserProfileResponse(email, profileImage);
	}
}
