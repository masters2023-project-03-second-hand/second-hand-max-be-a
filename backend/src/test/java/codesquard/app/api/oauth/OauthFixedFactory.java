package codesquard.app.api.oauth;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import codesquard.app.api.oauth.request.OauthLoginRequest;
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

	public static OauthSignUpRequest createFixedOauthSignUpRequest(String loginId, String addrName) {
		return OauthSignUpRequest.create(loginId, addrName);
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
		File catFile = new ClassPathResource("cat.png").getFile();
		String filename = catFile.getName().split("\\.")[0];
		String originalFilename = catFile.getName();
		String contentType = "multipart/form-data";
		byte[] content = Files.readAllBytes(catFile.toPath());
		ByteArrayInputStream mockInputStream = new ByteArrayInputStream(content);
		return new MockMultipartFile(filename, originalFilename, contentType, mockInputStream);
	}

	public static MockMultipartFile createFixedSignUpData(OauthSignUpRequest request) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		String requestJson = objectMapper.writeValueAsString(request);
		return new MockMultipartFile("signupData", "signupData", "application/json",
			requestJson.getBytes(StandardCharsets.UTF_8));
	}

	public static OauthLoginRequest createFixedOauthLoginRequest() {
		return new OauthLoginRequest("23Yong");
	}
}
