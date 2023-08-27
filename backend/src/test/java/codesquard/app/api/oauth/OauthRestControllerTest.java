package codesquard.app.api.oauth;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import codesquard.app.ControllerTestSupport;
import codesquard.app.api.oauth.request.OauthSignUpRequest;
import codesquard.app.api.oauth.response.OauthSignUpResponse;

class OauthRestControllerTest extends ControllerTestSupport {

	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(new OauthRestController(oauthService))
			.alwaysDo(print())
			.build();
	}

	@DisplayName("프로필 사진, 로그인 아이디, 동네를 전달하여 소셜 로그인 인증을 하고 회원가입을 한다")
	@Test
	public void signup() throws Exception {
		// given
		String filename = "profile";
		String originalFilename = "profile.jpg";
		String contentType = "image/jpeg";
		String content = "테스트 이미지 데이터";
		ByteArrayInputStream mockInputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
		MockMultipartFile mockProfile = new MockMultipartFile(filename, originalFilename, contentType, mockInputStream);
		String requestJson = objectMapper.writeValueAsString(new OauthSignUpRequest("23Yong", "가락 1동"));
		MockMultipartFile mockSignupData = new MockMultipartFile("signupData", "signupData", "application/json",
			requestJson.getBytes(StandardCharsets.UTF_8));

		// mocking
		when(oauthService.signUp(any(OauthSignUpRequest.class), anyString(), anyString()))
			.thenReturn(new OauthSignUpResponse(1L, "avatarUrlValue", "23Yong1234", "23Yong"));
		// when & then
		mockMvc.perform(multipart("/api/auth/naver/signup")
				.file(mockProfile)
				.file(mockSignupData)
				.param("code", "1234"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("statusCode").value(Matchers.equalTo(201)))
			.andExpect(jsonPath("message").value(Matchers.equalTo("CREATED")))
			.andExpect(jsonPath("data.id").value(Matchers.equalTo(1)))
			.andExpect(jsonPath("data.avatarUrl").value(Matchers.equalTo("avatarUrlValue")))
			.andExpect(jsonPath("data.socialLoginId").value(Matchers.equalTo("23Yong1234")))
			.andExpect(jsonPath("data.nickname").value(Matchers.equalTo("23Yong")));
	}
}