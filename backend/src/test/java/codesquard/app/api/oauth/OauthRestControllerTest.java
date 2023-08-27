package codesquard.app.api.oauth;

import static codesquard.app.api.oauth.OauthFixedFactory.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import codesquard.app.ControllerTestSupport;
import codesquard.app.api.oauth.request.OauthSignUpRequest;

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
		MockMultipartFile mockProfile = createFixedProfile();
		MockMultipartFile mockSignupData = createFixedSignUpData();
		// mocking
		when(oauthService.signUp(any(OauthSignUpRequest.class), anyString(), anyString()))
			.thenReturn(createdFixedOauthSignUpResponse());
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