package codesquard.app.api.oauth;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.mock.web.MockMultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import codesquard.app.api.oauth.request.OauthSignUpRequest;
import codesquard.app.api.oauth.response.OauthAccessTokenResponse;
import codesquard.app.api.oauth.response.OauthSignUpResponse;
import codesquard.app.api.oauth.response.OauthUserProfileResponse;

public class OauthFixedFactory {

	private static final String LOGIN_ID = "23Yong";
	private static final String ADDR_NAME = "가락 1동";
	private static final String ACCESS_TOKEN = "accessTokenValue";
	private static final String SCOPE = "scopeValue";
	private static final String TOKEN_TYPE = "Bearer";

	private static final String EMAIL = "23Yong@gmail.com";

	private static final String AVATAR_URL = "avatarUrlValue";

	public static OauthSignUpRequest createFixedOauthSignUpRequest() {
		return OauthSignUpRequest.create(LOGIN_ID, ADDR_NAME);
	}

	public static OauthAccessTokenResponse createFixedOauthAccessTokenResponse() {
		return OauthAccessTokenResponse.create(ACCESS_TOKEN, SCOPE, TOKEN_TYPE);
	}

	public static OauthUserProfileResponse createOauthUserProfileResponse() {
		return OauthUserProfileResponse.create(EMAIL);
	}

	public static OauthSignUpResponse createdFixedOauthSignUpResponse() {
		return OauthSignUpResponse.create(1L, AVATAR_URL, EMAIL, LOGIN_ID);
	}

	public static MockMultipartFile createFixedProfile() throws IOException {
		String filename = "profile";
		String originalFilename = "profile.jpg";
		String contentType = "image/jpeg";
		String content = "테스트 이미지 데이터";
		ByteArrayInputStream mockInputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
		return new MockMultipartFile(filename, originalFilename, contentType, mockInputStream);
	}

	public static MockMultipartFile createFixedSignUpData() throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		String requestJson = objectMapper.writeValueAsString(createFixedOauthSignUpRequest());
		return new MockMultipartFile("signupData", "signupData", "application/json",
			requestJson.getBytes(StandardCharsets.UTF_8));
	}
}
