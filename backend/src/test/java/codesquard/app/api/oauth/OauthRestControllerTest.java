package codesquard.app.api.oauth;

import static codesquard.app.api.oauth.OauthFixedFactory.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.stream.Stream;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import codesquard.app.ControllerTestSupport;
import codesquard.app.api.errors.handler.GlobalExceptionHandler;
import codesquard.app.api.oauth.request.OauthSignUpRequest;

class OauthRestControllerTest extends ControllerTestSupport {

	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(new OauthRestController(oauthService))
			.setControllerAdvice(new GlobalExceptionHandler())
			.alwaysDo(print())
			.build();
	}

	@DisplayName("프로필 사진, 로그인 아이디, 동네를 전달하여 소셜 로그인 인증을 하고 회원가입을 한다")
	@Test
	public void signup() throws Exception {
		// given
		MockMultipartFile mockProfile = createFixedProfile();
		MockMultipartFile mockSignupData = createFixedSignUpData(createFixedOauthSignUpRequest());

		// mocking
		when(oauthService.signUp(any(), any(OauthSignUpRequest.class), anyString(), anyString()))
			.thenReturn(createdFixedOauthSignUpResponse());

		// when & then
		mockMvc.perform(multipart("/api/auth/naver/signup")
				.file(mockProfile)
				.file(mockSignupData)
				.param("code", "1234"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("statusCode").value(Matchers.equalTo(201)));
	}

	@DisplayName("입력 형식에 맞지 않는 로그인 아이디를 전달하여 회원가입을 요청할때 에러를 응답한다")
	@MethodSource(value = "provideInvalidLoginId")
	@ParameterizedTest
	public void signupWhenInvalidLoginId(String loginId) throws Exception {
		// given
		MockMultipartFile mockProfile = createFixedProfile();
		MockMultipartFile mockSignupData = createFixedSignUpData(createFixedOauthSignUpRequest(loginId, "가락 1동"));

		// when & then
		mockMvc.perform(multipart("/api/auth/naver/signup")
				.file(mockProfile)
				.file(mockSignupData)
				.param("code", "1234"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("statusCode").value(Matchers.equalTo(400)))
			.andExpect(jsonPath("message").value(Matchers.equalTo("유효하지 않은 입력형식입니다.")))
			.andExpect(jsonPath("data[0].field").value(Matchers.equalTo("loginId")))
			.andExpect(jsonPath("data[0].defaultMessage").value(
				Matchers.equalTo("아이디는 띄어쓰기 없이 영문, 숫자로 구성되며 2~12글자로 구성되어야 합니다.")));
	}

	@DisplayName("비어 있는 주소를 전달하여 회원가입을 요청할 때 에러를 응답한다")
	@MethodSource(value = "provideInvalidAddrName")
	@ParameterizedTest
	public void signupWhenInvalidAddrName(String addrName) throws Exception {
		// given
		MockMultipartFile mockProfile = createFixedProfile();
		MockMultipartFile mockSignupData = createFixedSignUpData(createFixedOauthSignUpRequest("23Yong", addrName));

		// when & then
		mockMvc.perform(multipart("/api/auth/naver/signup")
				.file(mockProfile)
				.file(mockSignupData)
				.param("code", "1234"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("statusCode").value(Matchers.equalTo(400)))
			.andExpect(jsonPath("message").value(Matchers.equalTo("유효하지 않은 입력형식입니다.")))
			.andExpect(jsonPath("data[0].field").value(Matchers.equalTo("addrName")))
			.andExpect(jsonPath("data[0].defaultMessage").value(
				Matchers.equalTo("동네는 필수 정보입니다.")));
	}

	private static Stream<Arguments> provideInvalidLoginId() {
		return Stream.of(
			Arguments.of((Object)null),
			Arguments.of(""),
			Arguments.of(" "),
			Arguments.of("네모네모"),
			Arguments.of("aaaaaaaaaaaaaaaaaaaaaa"),
			Arguments.of("!@#!#AWEfa")
		);
	}

	private static Stream<Arguments> provideInvalidAddrName() {
		return Stream.of(
			Arguments.of((Object)null),
			Arguments.of("")
		);
	}
}
