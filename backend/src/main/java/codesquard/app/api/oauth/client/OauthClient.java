package codesquard.app.api.oauth.client;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import codesquard.app.api.oauth.response.OauthAccessTokenResponse;
import codesquard.app.api.oauth.response.OauthUserProfileResponse;
import codesquard.app.domain.oauth.OauthAttributes;
import codesquard.app.domain.oauth.OauthProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OauthClient {

	private static final Logger log = LoggerFactory.getLogger(OauthClient.class);

	// accessToken을 Oauth 서버로부터 발급받는다
	public OauthAccessTokenResponse exchangeAccessTokenByAuthorizationCode(OauthProvider oauthProvider,
		String authorizationCode) {
		String tokenUri = oauthProvider.getTokenUri();
		String clientId = oauthProvider.getClientId();
		String clientSecret = oauthProvider.getClientSecret();
		String redirectUri = oauthProvider.getRedirectUri();
		MultiValueMap<String, String> formData = createFormData(redirectUri, authorizationCode);

		return WebClient.create()
			.post()
			.uri(tokenUri)
			.headers(header -> {
				header.setBasicAuth(clientId, clientSecret);
				header.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // application/x-www-form-urlencoded
				header.setAccept(List.of(MediaType.APPLICATION_JSON));
				header.setAcceptCharset(List.of(StandardCharsets.UTF_8));
			})
			.bodyValue(formData)
			.retrieve() // ResponseEntity를 받아 디코딩
			.bodyToMono(OauthAccessTokenResponse.class) // 주어진 타입으로 디코딩
			.block();
	}

	private MultiValueMap<String, String> createFormData(String redirectUri, String authorizationCode) {
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("code", authorizationCode);
		formData.add("redirect_uri", redirectUri);
		formData.add("grant_type", "authorization_code");
		return formData;
	}

	// Oauth 리소스 서버로부터 유저의 프로필 가져온다
	public OauthUserProfileResponse getUserProfileByAccessToken(
		String providerName,
		OauthProvider oauthProvider,
		OauthAccessTokenResponse accessTokenResponse) {
		String userInfoUri = oauthProvider.getUserInfoUri();
		Map<String, Object> userProfileMap = getUserAttributes(userInfoUri, accessTokenResponse);
		log.debug("userProfileMap : {}", userProfileMap);
		return OauthAttributes.extract(providerName, userProfileMap);
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
}
