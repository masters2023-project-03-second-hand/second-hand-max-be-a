package codesquard.app.domain.oauth.client;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import codesquard.app.api.errors.errorcode.OauthErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.oauth.response.OauthAccessTokenResponse;
import codesquard.app.api.oauth.response.OauthUserProfileResponse;
import codesquard.app.domain.oauth.properties.OauthProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KakaoOauthClient extends OauthClient {

	public KakaoOauthClient(OauthProperties.Kakao kakao) {
		super(kakao.getClientId(),
			kakao.getClientSecret(),
			kakao.getTokenUri(),
			kakao.getUserInfoUri(),
			kakao.getRedirectUri());
	}

	@Override
	public OauthAccessTokenResponse exchangeAccessTokenByAuthorizationCode(String authorizationCode) {
		MultiValueMap<String, String> formData = createFormData(authorizationCode);

		OauthAccessTokenResponse response = WebClient.create()
			.post()
			.uri(getTokenUri())
			.headers(header -> {
				header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
				header.setAccept(List.of(MediaType.APPLICATION_JSON));
				header.setAcceptCharset(List.of(StandardCharsets.UTF_8));
			})
			.bodyValue(formData)
			.retrieve() // ResponseEntity를 받아 디코딩
			.bodyToMono(OauthAccessTokenResponse.class) // 주어진 타입으로 디코딩
			.block();

		if (Objects.requireNonNull(response).getAccessToken() == null) {
			throw new RestApiException(OauthErrorCode.WRONG_AUTHORIZATION_CODE);
		}

		return response;
	}

	@Override
	public MultiValueMap<String, String> createFormData(String authorizationCode) {
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("code", authorizationCode);
		formData.add("client_id", getClientId());
		formData.add("client_secret", getClientSecret());
		formData.add("redirect_uri", getRedirectUri());
		formData.add("grant_type", "authorization_code");

		return formData;
	}

	@Override
	public OauthUserProfileResponse createOauthUserProfileResponse(Map<String, Object> attributes) {
		Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>)kakaoAccount.get("profile");
		String email = (String)kakaoAccount.get("email");
		String profileImage = (String)profile.get("profile_image_url");
		return new OauthUserProfileResponse(email, profileImage);
	}
}
