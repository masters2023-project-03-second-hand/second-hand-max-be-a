package codesquard.app.api.oauth.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class OauthUserProfileResponse {
	private final String socialLoginId;
}
