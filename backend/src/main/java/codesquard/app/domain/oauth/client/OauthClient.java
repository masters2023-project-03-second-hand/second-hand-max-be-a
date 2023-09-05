package codesquard.app.domain.oauth.client;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import codesquard.app.api.errors.errorcode.OauthErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.oauth.response.OauthAccessTokenResponse;
import codesquard.app.api.oauth.response.OauthUserProfileResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class OauthClient {

	private final String clientId;
	private final String clientSecret;
	private final String tokenUri;
	private final String userInfoUri;
	private final String redirectUri;

	public OauthClient(String clientId, String clientSecret, String tokenUri, String userInfoUri, String redirectUri) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.tokenUri = tokenUri;
		this.userInfoUri = userInfoUri;
		this.redirectUri = redirectUri;
	}

	// accessToken을 Oauth 서버로부터 발급받는다
	public OauthAccessTokenResponse exchangeAccessTokenByAuthorizationCode(String authorizationCode) {
		MultiValueMap<String, String> formData = createFormData(redirectUri, authorizationCode);

		OauthAccessTokenResponse response = WebClient.create()
			.post()
			.uri(tokenUri)
			.headers(header -> {
				header.setBasicAuth(clientId, clientSecret);
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

	private MultiValueMap<String, String> createFormData(String redirectUri, String authorizationCode) {
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("code", authorizationCode);
		formData.add("redirect_uri", redirectUri);
		formData.add("grant_type", "authorization_code");
		return formData;
	}

	// Oauth 리소스 서버로부터 유저의 프로필 가져온다
	public OauthUserProfileResponse getUserProfileByAccessToken(OauthAccessTokenResponse accessTokenResponse) {
		Map<String, Object> userProfileMap = getUserAttributes(userInfoUri, accessTokenResponse);
		log.debug("userProfileMap : {}", userProfileMap);
		return createOauthUserProfileResponse(userProfileMap);
	}

	private Map<String, Object> getUserAttributes(String userInfoUri, OauthAccessTokenResponse accessTokenResponse) {
		return WebClient.create()
			.get()
			.uri(userInfoUri)
			.headers(header -> header.setBearerAuth(accessTokenResponse.getAccessToken()))
			.retrieve()
			.bodyToMono(
				new ParameterizedTypeReference<Map<String, Object>>() { // response body를 Map<String,Object> 형태로 변환
				})
			.block();
	}

	public abstract OauthUserProfileResponse createOauthUserProfileResponse(Map<String, Object> attributes);
}
