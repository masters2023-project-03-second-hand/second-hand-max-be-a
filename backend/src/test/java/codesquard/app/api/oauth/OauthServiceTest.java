package codesquard.app.api.oauth;

import static codesquard.app.api.oauth.OauthFixedFactory.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import codesquard.app.IntegrationTestSupport;
import codesquard.app.api.errors.errorcode.MemberErrorCode;
import codesquard.app.api.errors.errorcode.OauthErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.image.ImageService;
import codesquard.app.api.oauth.request.OauthLoginRequest;
import codesquard.app.api.oauth.request.OauthLogoutRequest;
import codesquard.app.api.oauth.request.OauthRefreshRequest;
import codesquard.app.api.oauth.request.OauthSignUpRequest;
import codesquard.app.api.oauth.response.OauthAccessTokenResponse;
import codesquard.app.api.oauth.response.OauthLoginResponse;
import codesquard.app.api.oauth.response.OauthLogoutResponse;
import codesquard.app.api.oauth.response.OauthRefreshResponse;
import codesquard.app.api.oauth.response.OauthSignUpResponse;
import codesquard.app.api.oauth.response.OauthUserProfileResponse;
import codesquard.app.domain.jwt.Jwt;
import codesquard.app.domain.jwt.JwtProvider;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.membertown.MemberTown;
import codesquard.app.domain.oauth.client.OauthClient;
import codesquard.app.domain.oauth.repository.OauthClientRepository;
import codesquard.app.domain.oauth.support.Principal;

class OauthServiceTest extends IntegrationTestSupport {

	@MockBean
	private OauthClientRepository oauthClientRepository;

	@Mock
	private OauthClient oauthClient;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private JwtProvider jwtProvider;

	@MockBean
	private ImageService imageService;

	@DisplayName("로그인 아이디와 소셜 로그인을 하여 회원가입을 한다")
	@Test
	public void signUp() throws IOException {
		// given
		String provider = "naver";
		String code = "1234";
		MockMultipartFile profile = createFixedProfile();
		OauthSignUpRequest request = createFixedOauthSignUpRequest();
		OauthAccessTokenResponse mockAccessTokenResponse = createFixedOauthAccessTokenResponse();
		OauthUserProfileResponse mockUserProfileResponse = createOauthUserProfileResponse();

		// mocking
		when(oauthClientRepository.findOneBy(anyString())).thenReturn(oauthClient);
		when(oauthClient.exchangeAccessTokenByAuthorizationCode(anyString()))
			.thenReturn(mockAccessTokenResponse);
		when(oauthClient.getUserProfileByAccessToken(any(OauthAccessTokenResponse.class)))
			.thenReturn(mockUserProfileResponse);
		when(imageService.uploadImage(any())).thenReturn("avatarUrlValue");

		// when
		OauthSignUpResponse response = oauthService.signUp(profile, request, provider, code);

		// then
		Member findMember = memberRepository.findMemberByLoginId("23Yong")
			.orElseThrow(() -> new RestApiException(MemberErrorCode.NOT_FOUND_MEMBER));

		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(response)
				.extracting("email", "loginId", "avatarUrl")
				.contains("23Yong@gmail.com", "23Yong", "avatarUrlValue");
			softAssertions.assertThat(findMember)
				.extracting("email", "loginId", "avatarUrl")
				.contains("23Yong@gmail.com", "23Yong", "avatarUrlValue");
			softAssertions.assertAll();
		});
	}

	@DisplayName("제공되지 않은 provider로 소셜 로그인하여 회원가입을 할 수 없다")
	@Test
	public void signUpWithInvalidProvider() throws IOException {
		// given
		String provider = "github";
		String code = "1234";
		MockMultipartFile profile = createFixedProfile();
		OauthSignUpRequest request = createFixedOauthSignUpRequest();

		// mocking
		when(oauthClientRepository.findOneBy(anyString())).thenThrow(
			new RestApiException(OauthErrorCode.NOT_FOUND_PROVIDER));
		// when
		Throwable throwable = catchThrowable(() -> oauthService.signUp(profile, request, provider, code));

		// then
		assertThat(throwable)
			.isInstanceOf(RestApiException.class)
			.extracting("errorCode")
			.extracting("name", "httpStatus", "message")
			.containsExactlyInAnyOrder("NOT_FOUND_PROVIDER", HttpStatus.NOT_FOUND, "provider를 찾을 수 없습니다.");
	}

	@DisplayName("잘못된 인가 코드로 소셜 로그인하여 회원가입을 할 수 없다")
	@Test
	public void signUpWithInvalidCode() throws IOException {
		// given
		String provider = "naver";
		String code = "1234";
		MockMultipartFile profile = createFixedProfile();
		OauthSignUpRequest request = createFixedOauthSignUpRequest();
		OauthAccessTokenResponse mockAccessTokenResponse = createFixedOauthAccessTokenResponse();

		// mocking
		when(oauthClientRepository.findOneBy(anyString())).thenReturn(oauthClient);
		when(oauthClient.exchangeAccessTokenByAuthorizationCode(anyString()))
			.thenReturn(mockAccessTokenResponse);
		when(oauthClient.getUserProfileByAccessToken(any(OauthAccessTokenResponse.class)))
			.thenThrow(new RestApiException(OauthErrorCode.WRONG_AUTHORIZATION_CODE));

		// when
		Throwable throwable = catchThrowable(() -> oauthService.signUp(profile, request, provider, code));

		// then
		assertThat(throwable)
			.isInstanceOf(RestApiException.class)
			.extracting("errorCode")
			.extracting("name", "httpStatus", "message")
			.containsExactlyInAnyOrder("WRONG_AUTHORIZATION_CODE", HttpStatus.UNAUTHORIZED, "잘못된 인가 코드입니다.");
	}

	@DisplayName("로그인 아이디가 중복되는 경우 회원가입을 할 수 없다")
	@Test
	public void signUpWhenDuplicateLoginId() throws IOException {
		// given
		Member member = Member.create("avatarUrlValue", "23Yong1234@gmail.com", "23Yong");
		MemberTown memberTown = MemberTown.create("가락 1동");
		member.addMemberTown(memberTown);
		memberRepository.save(member);

		String provider = "naver";
		String code = "1234";
		MockMultipartFile profile = createFixedProfile();
		OauthSignUpRequest request = createFixedOauthSignUpRequest();
		OauthAccessTokenResponse mockAccessTokenResponse = createFixedOauthAccessTokenResponse();
		OauthUserProfileResponse mockUserProfileResponse = createOauthUserProfileResponse();

		// mocking
		when(oauthClientRepository.findOneBy(anyString())).thenReturn(oauthClient);
		when(oauthClient.exchangeAccessTokenByAuthorizationCode(anyString()))
			.thenReturn(mockAccessTokenResponse);
		when(oauthClient.getUserProfileByAccessToken(any(OauthAccessTokenResponse.class)))
			.thenReturn(mockUserProfileResponse);

		// when
		Throwable throwable = catchThrowable(() -> oauthService.signUp(profile, request, provider, code));

		// then
		assertThat(throwable)
			.isInstanceOf(RestApiException.class)
			.extracting("errorCode.message")
			.isEqualTo("이미 존재하는 아이디입니다.");
	}

	@DisplayName("로그인 아이디와 인가코드를 가지고 소셜 로그인을 한다")
	@Test
	public void login() {
		// given
		Member member = createFixedMember();
		memberRepository.save(member);

		OauthLoginRequest request = createFixedOauthLoginRequest();
		String provider = "naver";
		String code = "1234";
		OauthAccessTokenResponse mockAccessTokenResponse = createFixedOauthAccessTokenResponse();
		OauthUserProfileResponse mockUserProfileResponse = createOauthUserProfileResponse();

		LocalDateTime now = createNow();
		// mocking
		when(oauthClientRepository.findOneBy(anyString())).thenReturn(oauthClient);
		when(oauthClient.exchangeAccessTokenByAuthorizationCode(anyString()))
			.thenReturn(mockAccessTokenResponse);
		when(oauthClient.getUserProfileByAccessToken(any(OauthAccessTokenResponse.class)))
			.thenReturn(mockUserProfileResponse);

		// when
		OauthLoginResponse response = oauthService.login(request, provider, code, now);

		// then
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(response)
				.extracting("jwt.accessToken", "jwt.refreshToken", "user.loginId", "user.profileUrl")
				.contains(
					createExpectedAccessTokenBy(jwtProvider, member, now),
					createExpectedRefreshTokenBy(jwtProvider, member, now),
					"23Yong",
					"avatarUrlValue");
			softAssertions.assertAll();
		});
	}

	@DisplayName("로그아웃을 수행한다")
	@Test
	public void logout() {
		// given
		Member member = OauthFixedFactory.createFixedMember();
		Member saveMember = memberRepository.save(member);
		LocalDateTime now = createNow();
		Jwt jwt = jwtProvider.createJwtBasedOnMember(member, now);
		Principal principal = jwtProvider.extractPrincipal(jwt.getAccessToken());
		OauthLogoutRequest request = OauthLogoutRequest.create(principal);

		// when
		OauthLogoutResponse response = oauthService.logout(request);

		// then
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(response)
				.extracting("id", "email")
				.contains(saveMember.getId(), "23Yong@gmail.com");
			softAssertions.assertThat(redisTemplate.opsForValue().get(principal.getAccessToken())).isEqualTo("logout");
			softAssertions.assertAll();
		});
	}

	@DisplayName("리프레쉬 토큰을 가지고 액세스 토큰을 갱신한다")
	@Test
	public void refreshAccessToken() {
		// given
		String avatarUrl = "avatarUrlValue";
		String loginId = "23Yong";
		String email = "23Yong@gmail.com";
		Member member = Member.create(avatarUrl, email, loginId);
		LocalDateTime now = createNow();

		Jwt jwt = jwtProvider.createJwtBasedOnMember(member, now);

		redisTemplate.opsForValue().set(member.createRedisKey(),
			jwt.getRefreshToken(),
			jwt.getExpireDateRefreshTokenTime(),
			TimeUnit.MILLISECONDS);
		memberRepository.save(member);

		OauthRefreshRequest request = OauthRefreshRequest.create(jwt.getRefreshToken());

		// when
		OauthRefreshResponse response = oauthService.refreshAccessToken(request, now);

		// then
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(response)
				.extracting("jwt.accessToken", "jwt.refreshToken")
				.contains(createExpectedAccessTokenBy(jwtProvider, member, now),
					createExpectedRefreshTokenBy(jwtProvider, member, now));
			softAssertions.assertAll();
		});
	}

	@DisplayName("유효하지 않은 토큰으로는 액세스 토큰을 갱신할 수 없다")
	@Test
	public void refreshAccessTokenWithInvalidRefreshToken() {
		// given
		Member member = createFixedMember();
		LocalDateTime now = createNow();

		Jwt jwt = jwtProvider.createJwtBasedOnMember(member, now);

		redisTemplate.opsForValue().set(member.createRedisKey(),
			jwt.getRefreshToken(),
			jwt.getExpireDateRefreshTokenTime(),
			TimeUnit.MILLISECONDS);
		memberRepository.save(member);

		OauthRefreshRequest request = OauthRefreshRequest.create("invalidRefreshTokenValue");

		// when
		Throwable throwable = catchThrowable(() -> oauthService.refreshAccessToken(request, now));

		// then
		Assertions.assertThat(throwable)
			.isInstanceOf(RestApiException.class)
			.extracting("errorCode.message")
			.isEqualTo("유효하지 않은 토큰입니다.");
	}
}
