package codesquard.app.api.oauth.response;

import codesquard.app.domain.jwt.Jwt;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class OauthLoginResponse {

	private Jwt jwt;
	private OauthLoginMemberResponse user;

	private OauthLoginResponse() {

	}

	private OauthLoginResponse(Jwt jwt, OauthLoginMemberResponse user) {
		this.jwt = jwt;
		this.user = user;
	}

	public static OauthLoginResponse create(Jwt jwt, OauthLoginMemberResponse user) {
		return new OauthLoginResponse(jwt, user);
	}
}
