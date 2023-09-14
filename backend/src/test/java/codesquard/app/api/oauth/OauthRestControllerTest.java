package codesquard.app.api.oauth;

import static codesquard.app.api.oauth.OauthFixedFactory.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.http.HttpHeaders;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import codesquard.app.ControllerTestSupport;
import codesquard.app.api.errors.handler.GlobalExceptionHandler;
import codesquard.app.api.oauth.request.OauthSignUpRequest;
import codesquard.app.api.oauth.response.OauthRefreshResponse;
import codesquard.app.domain.jwt.Jwt;
import codesquard.app.domain.oauth.support.AuthPrincipalArgumentResolver;
import codesquard.app.domain.oauth.support.Principal;
import codesquard.app.filter.JwtAuthorizationFilter;
import codesquard.app.filter.LogoutFilter;
import codesquard.app.interceptor.LogoutInterceptor;

class OauthRestControllerTest extends ControllerTestSupport {

	private MockMvc mockMvc;

	@Mock
	ValueOperations<String, Object> valueOperations;

	@MockBean
	private AuthPrincipalArgumentResolver authPrincipalArgumentResolver;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(new OauthRestController(oauthService))
			.setControllerAdvice(new GlobalExceptionHandler())
			.addFilters(
				new JwtAuthorizationFilter(jwtProvider, authenticationContext, objectMapper, redisTemplate),
				new LogoutFilter(redisTemplate, objectMapper)
			)
			.addMappedInterceptors(new String[] {"/api/auth/logout"}, new LogoutInterceptor())
			.setCustomArgumentResolvers(authPrincipalArgumentResolver)
			.alwaysDo(print())
			.build();
	}

	@DisplayName("프로필 사진, 로그인 아이디, 동네를 전달하여 소셜 로그인 인증을 하고 회원가입을 한다")
	@Test
	public void signup() throws Exception {
		// given
		MockMultipartFile mockProfile = createFixedProfile();
		MockMultipartFile mockSignupData = createFixedSignUpData(createFixedOauthSignUpRequest(List.of(1L)));

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
		MockMultipartFile mockSignupData = createFixedSignUpData(
			createFixedOauthSignUpRequest(loginId, List.of(1L)));

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

	@DisplayName("유효하지 않은 입력 형식의 주소 등록번호를 전달하여 회원가입을 요청할 때 에러를 응답한다")
	@MethodSource(value = "provideInvalidAddressIds")
	@ParameterizedTest
	public void signupWhenInvalidAddrName(List<Long> addressIds) throws Exception {
		// given
		MockMultipartFile mockProfile = createFixedProfile();
		MockMultipartFile mockSignupData = createFixedSignUpData(createFixedOauthSignUpRequest("23Yong", addressIds));

		// when & then
		mockMvc.perform(multipart("/api/auth/naver/signup")
				.file(mockProfile)
				.file(mockSignupData)
				.param("code", "1234"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("statusCode").value(Matchers.equalTo(400)))
			.andExpect(jsonPath("message").value(Matchers.equalTo("유효하지 않은 입력형식입니다.")))
			.andExpect(jsonPath("data[0].field").value(Matchers.equalTo("addressIds")))
			.andExpect(jsonPath("data[0].defaultMessage").value(
				Matchers.equalTo("동네 주소는 최소 1개 최대 2개를 입력해주세요.")));
	}

	@DisplayName("로그아웃을 요청한다")
	@Test
	public void logout() throws Exception {
		// given
		Map<String, String> map = new HashMap<>();
		map.put("refreshToken", "refreshTokenValue");

		given(redisTemplate.opsForValue()).willReturn(valueOperations);
		given(valueOperations.get(anyString())).willReturn(null);
		// when & then
		mockMvc.perform(post("/api/auth/logout")
				.header(HttpHeaders.AUTHORIZATION, "Bearer accessTokenValue")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(map))
				.characterEncoding(StandardCharsets.UTF_8)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("statusCode").value(Matchers.equalTo(200)))
			.andExpect(jsonPath("message").value(Matchers.equalTo("로그아웃에 성공하였습니다.")))
			.andExpect(jsonPath("data").value(Matchers.equalTo(null)));
	}

	@DisplayName("액세스 토큰 갱신을 요청한다")
	@Test
	public void refreshAccessToken() throws Exception {
		// given
		Principal principal = Principal.from(createFixedMember());
		OauthRefreshResponse response = OauthRefreshResponse.create(
			Jwt.create("accessTokenValue", "refreshTokenValue", null, null));
		// mocking
		when(authPrincipalArgumentResolver.supportsParameter(any())).thenReturn(true);
		when(authPrincipalArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(principal);
		when(oauthService.refreshAccessToken(any(), any())).thenReturn(response);
		// when & then
		mockMvc.perform(post("/api/auth/token")
				.content(objectMapper.writeValueAsString("refreshTokenValue"))
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("statusCode").value(Matchers.equalTo(200)))
			.andExpect(jsonPath("message").value(Matchers.equalTo("액세스 토큰 갱신에 성공하였습니다.")))
			.andExpect(jsonPath("data.accessToken").isNotEmpty());
	}

	private static Stream<Arguments> provideInvalidLoginId() {
		return Stream.of(
			Arguments.of((Object)null),
			Arguments.of(""),
			Arguments.of(" ")
		);
	}

	private static Stream<Arguments> provideInvalidAddressIds() {
		return Stream.of(
			Arguments.of((Object)null),
			Arguments.of(List.of()),
			Arguments.of(List.of(0L)),
			Arguments.of(List.of(1L, 2L, 3L))
		);
	}
}
