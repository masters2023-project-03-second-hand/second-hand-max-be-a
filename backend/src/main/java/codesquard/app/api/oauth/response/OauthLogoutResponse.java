package codesquard.app.api.oauth.response;

import codesquard.app.domain.oauth.support.Principal;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class OauthLogoutResponse {
	private Long id;
	private String email;

	private OauthLogoutResponse() {

	}

	public OauthLogoutResponse(Long id, String email) {
		this.id = id;
		this.email = email;
	}

	public static OauthLogoutResponse from(Principal principal) {
		return new OauthLogoutResponse(principal.getMemberId(), principal.getEmail());
	}
}
