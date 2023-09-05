package codesquard.app.api.oauth;

import static codesquard.app.api.oauth.OauthFixedFactory.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
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
import codesquard.app.domain.member.AuthenticateMember;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.oauth.support.AuthPrincipalArgumentResolver;
import codesquard.app.domain.oauth.support.Principal;
import codesquard.app.filter.JwtAuthorizationFilter;
import io.jsonwebtoken.Claims;

class OauthRestControllerTest extends ControllerTestSupport {

	private MockMvc mockMvc;

	@Mock
	private ValueOperations<String, Object> valueOperations;

	@MockBean
	private AuthPrincipalArgumentResolver authPrincipalArgumentResolver;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(new OauthRestController(oauthService))
			.setControllerAdvice(new GlobalExceptionHandler())
			.addFilter(new JwtAuthorizationFilter(jwtProvider, authenticationContext))
			.setCustomArgumentResolvers(authPrincipalArgumentResolver)
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
		MockMultipartFile mockSignupData = createFixedSignUpData(
			createFixedOauthSignUpRequest(loginId, List.of("가락 1동")));

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
				Matchers.equalTo("로그인 아이디는 필수 정보입니다.")));
	}

	@DisplayName("비어 있는 주소를 전달하여 회원가입을 요청할 때 에러를 응답한다")
	@MethodSource(value = "provideInvalidAddrName")
	@ParameterizedTest
	public void signupWhenInvalidAddrName(String addrName) throws Exception {
		// given
		List<String> addressNames = new ArrayList<>();
		addressNames.add(addrName);
		MockMultipartFile mockProfile = createFixedProfile();
		MockMultipartFile mockSignupData = createFixedSignUpData(createFixedOauthSignUpRequest("23Yong", addressNames));

		// when & then
		mockMvc.perform(multipart("/api/auth/naver/signup")
				.file(mockProfile)
				.file(mockSignupData)
				.param("code", "1234"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("statusCode").value(Matchers.equalTo(400)))
			.andExpect(jsonPath("message").value(Matchers.equalTo("유효하지 않은 입력형식입니다.")))
			.andExpect(jsonPath("data[0].field").value(Matchers.equalTo("addressNames[0]")))
			.andExpect(jsonPath("data[0].defaultMessage").value(
				Matchers.equalTo("주소 이름은 공백이면 안됩니다.")));
	}

	@DisplayName("로그아웃을 요청한다")
	@Test
	public void logout() throws Exception {
		// given
		String avatarUrl = "avatarUrlValue";
		String loginId = "23Yong";
		String email = "23Yong@gmail.com";
		Member member = Member.create(avatarUrl, email, loginId);
		AuthenticateMember authMember = AuthenticateMember.from(member);

		Claims claims = mock(Claims.class);
		String authMemberJson = objectMapper.writeValueAsString(authMember);

		// mocking
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(anyString())).thenReturn(null);
		when(jwtProvider.getClaims(anyString())).thenReturn(claims);
		when(claims.get(anyString(), any())).thenReturn(authMemberJson);

		// when & then
		mockMvc.perform(post("/api/auth/logout")
				.header(HttpHeaders.AUTHORIZATION, "Bearer accessTokenValue")
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
			.andExpect(jsonPath("data.jwt.accessToken").isNotEmpty())
			.andExpect(jsonPath("data.jwt.refreshToken").isNotEmpty());
	}

	private static Stream<Arguments> provideInvalidLoginId() {
		return Stream.of(
			Arguments.of((Object)null),
			Arguments.of(""),
			Arguments.of(" ")
		);
	}

	private static Stream<Arguments> provideInvalidAddrName() {
		return Stream.of(
			Arguments.of((Object)null),
			Arguments.of("")
		);
	}
}
