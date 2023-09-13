package codesquard.app.api.oauth.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OauthLogoutResponse {

	private String email;

	public static OauthLogoutResponse from(String email) {
		return new OauthLogoutResponse(email);
	}

	@Override
	public String toString() {
		return String.format("%s, %s(email=%s)", "로그아웃 응답", this.getClass().getSimpleName(), email);
	}
}
