package codesquard.app.api.oauth.response;

import codesquard.app.domain.oauth.support.Principal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OauthLogoutResponse {
	private Long id;
	private String email;

	public OauthLogoutResponse(Long id, String email) {
		this.id = id;
		this.email = email;
	}

	public static OauthLogoutResponse from(Principal principal) {
		return new OauthLogoutResponse(principal.getMemberId(), principal.getEmail());
	}

	@Override
	public String toString() {
		return String.format("%s, %s(id=%d, email=%s)", "로그아웃 응답", this.getClass().getSimpleName(), id, email);
	}
}
