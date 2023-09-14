package codesquard.app.api.oauth.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OauthLogoutRequest {

	private String accessToken;
	private String refreshToken;

	public static OauthLogoutRequest create(String accessToken, String refreshToken) {
		return new OauthLogoutRequest(accessToken, refreshToken);
	}

	@Override
	public String toString() {
		return String.format("%s, %s(accessToken=%s, refreshToken=%s)", "로그아웃 요청", this.getClass().getSimpleName(),
			accessToken, refreshToken);
	}
}
