package codesquard.app.api.oauth.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OauthAccessTokenResponse {

	@JsonProperty("access_token")
	private String accessToken;
	@JsonProperty("scope")
	private String scope;
	@JsonProperty("token_type")
	private String tokenType;

	public static OauthAccessTokenResponse create(String accessToken, String scope, String tokenType) {
		return new OauthAccessTokenResponse(accessToken, scope, tokenType);
	}

	@Override
	public String toString() {
		return String.format("%s, %s(scope=%s, tokenType=%s)", "액세스 토큰 발급 응답", this.getClass().getSimpleName(), scope,
			tokenType);
	}
}
