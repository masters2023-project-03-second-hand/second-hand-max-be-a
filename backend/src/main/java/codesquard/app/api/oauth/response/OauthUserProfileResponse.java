package codesquard.app.api.oauth.response;

import lombok.Getter;

@Getter
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

	@Override
	public String toString() {
		return String.format("%s, %s(email=%s, profileImage=%s)", "유저 프로필 응답", this.getClass().getSimpleName(), email,
			profileImage);
	}
}
