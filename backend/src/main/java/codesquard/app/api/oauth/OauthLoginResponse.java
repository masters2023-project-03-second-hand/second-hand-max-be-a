package codesquard.app.api.oauth;

import codesquard.app.domain.jwt.Jwt;
import codesquard.app.domain.member.Member;

public class OauthLoginResponse {
	private final Jwt jwt;
	private final Member user;

	public OauthLoginResponse(Jwt jwt, Member user) {
		this.jwt = jwt;
		this.user = user;
	}
}
