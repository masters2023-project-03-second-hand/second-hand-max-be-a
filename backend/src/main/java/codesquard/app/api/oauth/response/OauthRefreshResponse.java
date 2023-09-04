package codesquard.app.api.oauth.response;

import codesquard.app.domain.jwt.Jwt;
import lombok.Getter;

@Getter
public class OauthRefreshResponse {
	private Jwt jwt;

	private OauthRefreshResponse() {

	}

	private OauthRefreshResponse(Jwt jwt) {
		this.jwt = jwt;
	}

	public static OauthRefreshResponse create(Jwt jwt) {
		return new OauthRefreshResponse(jwt);
	}

	@Override
	public String toString() {
		return String.format("%s, %s(accessToken=%s)", "액세스 토큰 갱신 응답", this.getClass().getSimpleName(),
			jwt.getAccessToken());
	}
}
