package codesquard.app.api.oauth.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class OauthAccessTokenResponse {
	@Getter
	@JsonProperty("access_token")
	private String accessToken;
	@JsonProperty("scope")
	private String scope;
	@JsonProperty("token_type")
	private String tokenType;

	private OauthAccessTokenResponse(String accessToken, String scope, String tokenType) {
		this.accessToken = accessToken;
		this.scope = scope;
		this.tokenType = tokenType;
	}

	public static OauthAccessTokenResponse create(String accessToken, String scope, String tokenType) {
		return new OauthAccessTokenResponse(accessToken, scope, tokenType);
	}
}
