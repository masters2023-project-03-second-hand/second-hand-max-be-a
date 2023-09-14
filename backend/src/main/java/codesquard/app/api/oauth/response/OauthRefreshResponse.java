package codesquard.app.api.oauth.response;

import codesquard.app.domain.jwt.Jwt;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OauthRefreshResponse {
	private String accessToken;

	public static OauthRefreshResponse create(Jwt jwt) {
		return new OauthRefreshResponse(jwt.getAccessToken());
	}

	@Override
	public String toString() {
		return String.format("%s, %s(accessToken=%s)", "액세스 토큰 갱신 응답", this.getClass().getSimpleName(),
			accessToken);
	}
}
