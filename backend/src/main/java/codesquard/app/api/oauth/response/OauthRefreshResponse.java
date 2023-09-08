package codesquard.app.api.oauth.response;

import codesquard.app.domain.jwt.Jwt;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OauthRefreshResponse {
	private OauthJwtResponse jwt;

	private OauthRefreshResponse(OauthJwtResponse jwt) {
		this.jwt = jwt;
	}

	public static OauthRefreshResponse create(Jwt jwt) {
		return new OauthRefreshResponse(OauthJwtResponse.create(jwt));
	}

	@Override
	public String toString() {
		return String.format("%s, %s(accessToken=%s)", "액세스 토큰 갱신 응답", this.getClass().getSimpleName(),
			jwt.getAccessToken());
	}
}
