package codesquard.app.api.oauth;

import static codesquard.app.api.oauth.OauthFixedFactory.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
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
import codesquard.app.api.oauth.response.OauthRefreshResponse;
import codesquard.app.api.oauth.response.OauthSignUpResponse;
import codesquard.app.api.oauth.response.OauthUserProfileResponse;
import codesquard.app.domain.jwt.Jwt;
import codesquard.app.domain.jwt.JwtProvider;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.membertown.MemberTown;
import codesquard.app.domain.oauth.client.OauthClient;
import codesquard.app.domain.oauth.repository.OauthClientRepository;

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
		List<Long> addressIds = getAddressIds("서울 송파구 가락동");
		String provider = "naver";
		String code = "1234";
		MockMultipartFile profile = createFixedProfile();
		OauthSignUpRequest request = createFixedOauthSignUpRequest(addressIds);
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
		List<MemberTown> memberTowns = memberTownRepository.findAllByMemberId(findMember.getId());

		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(response)
				.extracting("email", "loginId", "avatarUrl")
				.contains("23Yong@naver.com", "23Yong", "avatarUrlValue");
			softAssertions.assertThat(findMember)
				.extracting("email", "loginId", "avatarUrl")
				.contains("23Yong@naver.com", "23Yong", "avatarUrlValue");
			softAssertions.assertThat(memberTowns).hasSize(1);
			softAssertions.assertAll();
		});
	}

	@DisplayName("중복된 로그인 아이디로 회원가입을 할 수 없다")
	@Test
	public void signupWithDuplicateLoginId() throws IOException {
		// given
		List<Long> addressIds = getAddressIds("서울 송파구 가락동");

		memberRepository.save(createFixedMember());
		String provider = "naver";
		String code = "1234";
		MockMultipartFile profile = createFixedProfile();
		OauthSignUpRequest request = createFixedOauthSignUpRequest(addressIds);

		// when
		Throwable throwable = catchThrowable(() -> oauthService.signUp(profile, request, provider, code));
		// then

		assertThat(throwable)
			.isInstanceOf(RestApiException.class)
			.extracting("errorCode.message")
			.isEqualTo("중복된 아이디입니다.");
	}

	@DisplayName("한 명의 소셜 사용자가 서로 다른 로그인 아이디로 이중 회원가입을 할 수 없다")
	@Test
	public void signupWithMultipleLoginId() throws IOException {
		// given
		imageRepository.deleteAllInBatch();
		itemRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
		memberRepository.save(createFixedMember());
		List<Long> addressIds = getAddressIds("서울 송파구 가락동");

		String provider = "naver";
		String code = "1234";
		MockMultipartFile profile = createFixedProfile();
		OauthSignUpRequest request = createOauthSignUpRequest("bruni2", addressIds);
		OauthAccessTokenResponse mockAccessTokenResponse = createFixedOauthAccessTokenResponse();
		OauthUserProfileResponse mockUserProfileResponse = createOauthUserProfileResponse();

		given(oauthClientRepository.findOneBy(anyString())).willReturn(oauthClient);
		given(oauthClient.exchangeAccessTokenByAuthorizationCode(anyString()))
			.willReturn(mockAccessTokenResponse);
		given(oauthClient.getUserProfileByAccessToken(any(OauthAccessTokenResponse.class)))
			.willReturn(mockUserProfileResponse);

		// when
		Throwable throwable = catchThrowable(() -> oauthService.signUp(profile, request, provider, code));

		// then

		assertThat(throwable)
			.isInstanceOf(RestApiException.class)
			.extracting("errorCode.message")
			.isEqualTo("이미 회원가입된 상태입니다.");
	}

	@DisplayName("제공되지 않은 provider로 소셜 로그인하여 회원가입을 할 수 없다")
	@Test
	public void signUpWithInvalidProvider() throws IOException {
		// given
		List<Long> addressIds = getAddressIds("서울 송파구 가락동");

		String provider = "github";
		String code = "1234";
		MockMultipartFile profile = createFixedProfile();
		OauthSignUpRequest request = createFixedOauthSignUpRequest(addressIds);

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
		List<Long> addressIds = getAddressIds("서울 송파구 가락동");

		String provider = "naver";
		String code = "1234";
		MockMultipartFile profile = createFixedProfile();
		OauthSignUpRequest request = createFixedOauthSignUpRequest(addressIds);
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
			.containsExactlyInAnyOrder("WRONG_AUTHORIZATION_CODE", HttpStatus.BAD_REQUEST, "잘못된 인가 코드입니다.");
	}

	@DisplayName("로그인 아이디가 중복되는 경우 회원가입을 할 수 없다")
	@Test
	public void signUpWhenDuplicateLoginId() throws IOException {
		// given
		List<Long> addressIds = getAddressIds("서울 송파구 가락동");
		Member member = Member.create("avatarUrlValue", "23Yong1234@gmail.com", "23Yong");
		MemberTown memberTown = MemberTown.create(getRegion("서울 송파구 가락동"), member);
		memberRepository.save(member);
		memberTownRepository.save(memberTown);

		String provider = "naver";
		String code = "1234";
		MockMultipartFile profile = createFixedProfile();
		OauthSignUpRequest request = createFixedOauthSignUpRequest(addressIds);
		OauthAccessTokenResponse mockAccessTokenResponse = createFixedOauthAccessTokenResponse();
		OauthUserProfileResponse mockUserProfileResponse = createOauthUserProfileResponse();

		given(oauthClientRepository.findOneBy(anyString()))
			.willReturn(oauthClient);
		given(oauthClient.exchangeAccessTokenByAuthorizationCode(anyString()))
			.willReturn(mockAccessTokenResponse);
		given(oauthClient.getUserProfileByAccessToken(any(OauthAccessTokenResponse.class)))
			.willReturn(mockUserProfileResponse);

		// when
		Throwable throwable = catchThrowable(() -> oauthService.signUp(profile, request, provider, code));

		// then
		assertThat(throwable)
			.isInstanceOf(RestApiException.class)
			.extracting("errorCode.message")
			.isEqualTo("중복된 아이디입니다.");
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
		LocalDateTime now = createNow();
		Jwt jwt = jwtProvider.createJwtBasedOnMember(member, now);
		OauthLogoutRequest request = OauthLogoutRequest.create(jwt.getAccessToken(), jwt.getRefreshToken());

		// when
		oauthService.logout(request);

		// then
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(redisTemplate.opsForValue().get(jwt.getAccessToken())).isEqualTo("logout");
			softAssertions.assertAll();
		});
	}

	@DisplayName("만료된 액세스 토큰을 가지고 로그아웃을 요청하면 블랙리스트에 추가하지 않는다")
	@Test
	public void logoutWithExpireAccessToken() {
		// given
		Member member = OauthFixedFactory.createFixedMember();
		LocalDateTime now = LocalDateTime.now().minusMinutes(5);
		Jwt jwt = jwtProvider.createJwtBasedOnMember(member, now);
		OauthLogoutRequest request = OauthLogoutRequest.create(jwt.getAccessToken(), jwt.getRefreshToken());

		// when
		oauthService.logout(request);

		// then
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(redisTemplate.opsForValue().get(jwt.getAccessToken())).isNull();
			softAssertions.assertAll();
		});
	}

	@DisplayName("만료된 리프레쉬 토큰을 가지고 로그아웃을 요청하면 이미 만료되어 아무 처리도 하지 않는다")
	@Test
	public void logoutWithExpireRefreshToken() {
		// given
		Member member = OauthFixedFactory.createFixedMember();
		LocalDateTime now = createNow().minusHours(10);
		Jwt jwt = jwtProvider.createJwtBasedOnMember(member, now);
		OauthLogoutRequest request = OauthLogoutRequest.create(jwt.getAccessToken(), jwt.getRefreshToken());

		redisTemplate.opsForValue()
			.set(member.createRedisKey(), jwt.getRefreshToken(),
				jwt.getExpireDateRefreshToken().getTime(), TimeUnit.MILLISECONDS);
		// when
		oauthService.logout(request);

		// then
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(redisTemplate.opsForValue().get(member.createRedisKey())).isNull();
			softAssertions.assertAll();
		});
	}

	@DisplayName("리프레쉬 토큰을 가지고 액세스 토큰을 갱신한다")
	@Test
	public void refreshAccessToken() {
		// given
		Member member = createFixedMember();
		LocalDateTime now = createNow();

		Jwt jwt = jwtProvider.createJwtBasedOnMember(member, now);

		redisTemplate.opsForValue().set(member.createRedisKey(),
			jwt.getRefreshToken(),
			jwt.convertExpireDateRefreshTokenTimeWithLong(),
			TimeUnit.MILLISECONDS);
		memberRepository.save(member);

		OauthRefreshRequest request = OauthRefreshRequest.create(jwt.getRefreshToken());

		// when
		OauthRefreshResponse response = oauthService.refreshAccessToken(request, now);

		// then
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(response)
				.extracting("accessToken")
				.isEqualTo(createExpectedAccessTokenBy(jwtProvider, member, now));
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
			jwt.convertExpireDateRefreshTokenTimeWithLong(),
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
