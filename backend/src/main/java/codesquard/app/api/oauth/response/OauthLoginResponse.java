package codesquard.app.api.oauth.response;

import codesquard.app.domain.jwt.Jwt;
import lombok.Getter;

@Getter
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

	@Override
	public String toString() {
		return String.format("%s, %s(loginId=%s)", "소셜 로그인 응답", this.getClass().getSimpleName(), user.getLoginId());
	}
}
