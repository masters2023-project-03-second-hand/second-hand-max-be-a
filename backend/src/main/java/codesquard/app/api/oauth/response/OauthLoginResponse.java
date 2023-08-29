package codesquard.app.api.oauth.response;

import codesquard.app.domain.jwt.Jwt;
import codesquard.app.domain.member.AuthenticateMember;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class OauthLoginResponse {
	private Jwt jwt;
	private AuthenticateMember user;

	private OauthLoginResponse() {

	}

	private OauthLoginResponse(Jwt jwt, AuthenticateMember user) {
		this.jwt = jwt;
		this.user = user;
	}

	public static OauthLoginResponse create(Jwt jwt, AuthenticateMember authMember) {
		return new OauthLoginResponse(jwt, authMember);
	}
}
