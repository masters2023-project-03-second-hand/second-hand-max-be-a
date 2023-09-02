package codesquard.app.api.oauth.response;

import codesquard.app.domain.jwt.Jwt;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
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
}
