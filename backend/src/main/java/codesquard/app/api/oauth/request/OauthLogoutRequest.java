package codesquard.app.api.oauth.request;

import codesquard.app.domain.oauth.support.Principal;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class OauthLogoutRequest {

	private final Principal principal;

	private OauthLogoutRequest(Principal principal) {
		this.principal = principal;
	}

	public static OauthLogoutRequest create(Principal principal) {
		return new OauthLogoutRequest(principal);
	}
}
