package codesquard.app.api.oauth.response;

import codesquard.app.domain.jwt.Jwt;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OauthJwtResponse {
	private String accessToken;

	private OauthJwtResponse(String accessToken) {
		this.accessToken = accessToken;
	}

	public static OauthJwtResponse create(Jwt jwt) {
		return new OauthJwtResponse(jwt.getAccessToken());
	}
}
