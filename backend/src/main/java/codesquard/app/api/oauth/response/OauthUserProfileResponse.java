package codesquard.app.api.oauth.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OauthUserProfileResponse {

	private String email;
	private String profileImage;

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
