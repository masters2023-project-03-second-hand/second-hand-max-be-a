package codesquard.app.api.oauth.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class OauthRefreshRequest {

	private String refreshToken;

	private OauthRefreshRequest() {

	}

	private OauthRefreshRequest(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public static OauthRefreshRequest create(String refreshToken) {
		return new OauthRefreshRequest(refreshToken);
	}

}
