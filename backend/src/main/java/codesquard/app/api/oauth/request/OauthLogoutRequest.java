package codesquard.app.api.oauth.request;

import codesquard.app.domain.jwt.Jwt;
import codesquard.app.domain.member.AuthenticateMember;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class OauthLogoutRequest {
	private AuthenticateMember authMember;

	private Jwt jwt;

	private OauthLogoutRequest(AuthenticateMember authMember, Jwt jwt) {
		this.authMember = authMember;
		this.jwt = jwt;
	}

	public static OauthLogoutRequest create(AuthenticateMember authMember, Jwt jwt) {
		return new OauthLogoutRequest(authMember, jwt);
	}
}
