package codesquard.app.api.oauth.request;

import codesquard.app.domain.oauth.support.Principal;
import lombok.Getter;

@Getter
public class OauthLogoutRequest {

	private final Principal principal;

	private OauthLogoutRequest(Principal principal) {
		this.principal = principal;
	}

	public static OauthLogoutRequest create(Principal principal) {
		return new OauthLogoutRequest(principal);
	}

	@Override
	public String toString() {
		return String.format("%s, %s(loginId=%s)", "로그아웃 요청", this.getClass().getSimpleName(), principal.getLoginId());
	}
}
