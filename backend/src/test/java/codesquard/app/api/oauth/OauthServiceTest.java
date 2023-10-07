package codesquard.app.api.oauth;

import static codesquard.app.ImageTestSupport.*;
import static codesquard.app.MemberTestSupport.*;
import static codesquard.app.RegionTestSupport.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import codesquard.app.api.errors.errorcode.MemberErrorCode;
import codesquard.app.api.errors.errorcode.OauthErrorCode;
import codesquard.app.api.errors.exception.BadRequestException;
import codesquard.app.api.errors.exception.ConflictException;
import codesquard.app.api.errors.exception.NotFoundResourceException;
import codesquard.app.api.errors.exception.UnAuthorizationException;
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
import codesquard.app.api.redis.OauthRedisService;
import codesquard.app.domain.jwt.Jwt;
import codesquard.app.domain.jwt.JwtProvider;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.member.MemberRepository;
import codesquard.app.domain.membertown.MemberTown;
import codesquard.app.domain.membertown.MemberTownRepository;
import codesquard.app.domain.oauth.client.OauthClient;
import codesquard.app.domain.oauth.repository.OauthClientRepository;
import codesquard.app.domain.region.Region;
import codesquard.app.domain.region.RegionRepository;

@ActiveProfiles("test")
@SpringBootTest
class OauthServiceTest {

	@MockBean
	private OauthClientRepository oauthClientRepository;

	@Mock
	private OauthClient oauthClient;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MemberTownRepository memberTownRepository;

	@Autowired
	private RegionRepository regionRepository;

	@Autowired
	private OauthService oauthService;

	@Autowired
	private JwtProvider jwtProvider;

	@MockBean
	private ImageService imageService;

	@Autowired
	private OauthRedisService redisService;

	@Autowired
	private ObjectMapper objectMapper;

	@AfterEach
	void tearDown() {
		memberTownRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
	}

	@DisplayName("로그인 아이디와 소셜 로그인을 하여 회원가입을 한다")
	@Test
	void signUp() throws IOException {
		// given
		regionRepository.save(createRegion("서울 송파구 가락동"));
		List<Long> addressIds = getAddressIds(regionRepository.findAllByNameIn(List.of("서울 송파구 가락동")));
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("loginId", "23Yong");
		requestBody.put("addressIds", addressIds);
		OauthSignUpRequest request = objectMapper.readValue(objectMapper.writeValueAsString(requestBody),
			OauthSignUpRequest.class);

		Map<String, Object> responseBody = new HashMap<>();
		responseBody.put("accessToken", "accessTokenValue");
		responseBody.put("scope", "scopeValue");
		responseBody.put("tokenType", "Bearer");
		OauthAccessTokenResponse mockAccessTokenResponse =
			objectMapper.readValue(objectMapper.writeValueAsString(responseBody), OauthAccessTokenResponse.class);

		Map<String, Object> userProfileResponseBody = new HashMap<>();
		userProfileResponseBody.put("email", "23Yong@gmail.com");
		userProfileResponseBody.put("profileImage", "profileImageValue");
		OauthUserProfileResponse mockUserProfileResponse = objectMapper.readValue(
			objectMapper.writeValueAsString(userProfileResponseBody), OauthUserProfileResponse.class);

		given(oauthClientRepository.findOneBy(anyString())).willReturn(oauthClient);
		given(oauthClient.exchangeAccessTokenByAuthorizationCode(anyString(), anyString()))
			.willReturn(mockAccessTokenResponse);
		given(oauthClient.getUserProfileByAccessToken(any(OauthAccessTokenResponse.class)))
			.willReturn(mockUserProfileResponse);
		given(imageService.uploadImage(any())).willReturn("avatarUrlValue");

		String provider = "naver";
		String code = "1234";
		// when
		OauthSignUpResponse response = oauthService.signUp(createMultipartFile("cat.png"), request, provider, code,
			"http://localhost:5173/my-account/oauth");

		// then
		Member findMember = memberRepository.findMemberByLoginId("23Yong")
			.orElseThrow(() -> new NotFoundResourceException(MemberErrorCode.NOT_FOUND_MEMBER));
		List<MemberTown> memberTowns = memberTownRepository.findAllByMemberId(findMember.getId());

		assertAll(() -> {
			assertThat(response)
				.extracting("email", "loginId", "avatarUrl")
				.contains("23Yong@gmail.com", "23Yong", "avatarUrlValue");
			assertThat(findMember)
				.extracting("email", "loginId", "avatarUrl")
				.contains("23Yong@gmail.com", "23Yong", "avatarUrlValue");
			assertThat(memberTowns).hasSize(1);
		});
	}

	@DisplayName("중복된 로그인 아이디로 회원가입을 할 수 없다")
	@Test
	void signupWithDuplicateLoginId() throws IOException {
		// given
		memberRepository.save(createMember("avatarUrlValue", "23Yong@gmail.com", "23Yong"));

		List<Long> addressIds = getAddressIds(regionRepository.findAllByNameIn(List.of("서울 송파구 가락동")));
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("loginId", "23Yong");
		requestBody.put("addressIds", addressIds);
		OauthSignUpRequest request = objectMapper.readValue(objectMapper.writeValueAsString(requestBody),
			OauthSignUpRequest.class);

		String provider = "naver";
		String code = "1234";
		// when
		Throwable throwable = catchThrowable(
			() -> oauthService.signUp(createMultipartFile("cat.png"), request, provider, code, null));
		// then

		assertThat(throwable)
			.isInstanceOf(ConflictException.class)
			.extracting("errorCode.message")
			.isEqualTo("중복된 아이디입니다.");
	}

	@DisplayName("한 명의 소셜 사용자가 서로 다른 로그인 아이디로 이중 회원가입을 할 수 없다")
	@Test
	void signupWithMultipleLoginId() throws IOException {
		// given
		memberRepository.deleteAllInBatch();
		memberRepository.save(createMember("avatarUrlValue", "23Yong@gmail.com", "23Yong"));

		List<Long> addressIds = getAddressIds(regionRepository.findAllByNameIn(List.of("서울 송파구 가락동")));
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("loginId", "bruni2");
		requestBody.put("addressIds", addressIds);
		OauthSignUpRequest request = objectMapper.readValue(objectMapper.writeValueAsString(requestBody),
			OauthSignUpRequest.class);

		Map<String, Object> responseBody = new HashMap<>();
		responseBody.put("accessToken", "accessTokenValue");
		responseBody.put("scope", "scopeValue");
		responseBody.put("tokenType", "Bearer");
		OauthAccessTokenResponse mockAccessTokenResponse =
			objectMapper.readValue(objectMapper.writeValueAsString(responseBody), OauthAccessTokenResponse.class);

		Map<String, Object> userProfileResponseBody = new HashMap<>();
		userProfileResponseBody.put("email", "23Yong@gmail.com");
		userProfileResponseBody.put("profileImage", "profileImageValue");
		OauthUserProfileResponse mockUserProfileResponse = objectMapper.readValue(
			objectMapper.writeValueAsString(userProfileResponseBody), OauthUserProfileResponse.class);

		given(oauthClientRepository.findOneBy(anyString())).willReturn(oauthClient);
		given(oauthClient.exchangeAccessTokenByAuthorizationCode(anyString(), anyString()))
			.willReturn(mockAccessTokenResponse);
		given(oauthClient.getUserProfileByAccessToken(any(OauthAccessTokenResponse.class)))
			.willReturn(mockUserProfileResponse);

		String provider = "naver";
		String code = "1234";
		// when
		Throwable throwable = catchThrowable(
			() -> oauthService.signUp(createMultipartFile("cat.png"), request, provider, code,
				"http://localhost:5173/my-account/oauth"));

		// then

		assertThat(throwable)
			.isInstanceOf(UnAuthorizationException.class)
			.extracting("errorCode.message")
			.isEqualTo("이미 회원가입된 상태입니다.");
	}

	@DisplayName("제공되지 않은 provider로 소셜 로그인하여 회원가입을 할 수 없다")
	@Test
	void signUpWithInvalidProvider() throws IOException {
		// given

		List<Long> addressIds = getAddressIds(regionRepository.findAllByNameIn(List.of("서울 송파구 가락동")));
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("loginId", "23Yong");
		requestBody.put("addressIds", addressIds);
		OauthSignUpRequest request = objectMapper.readValue(objectMapper.writeValueAsString(requestBody),
			OauthSignUpRequest.class);

		given(oauthClientRepository.findOneBy(anyString()))
			.willThrow(new NotFoundResourceException(OauthErrorCode.NOT_FOUND_PROVIDER));

		String provider = "github";
		String code = "1234";
		// when
		Throwable throwable = catchThrowable(
			() -> oauthService.signUp(createMultipartFile("cat.png"), request, provider, code, null));

		// then
		assertThat(throwable)
			.isInstanceOf(NotFoundResourceException.class)
			.extracting("errorCode")
			.extracting("name", "httpStatus", "message")
			.containsExactlyInAnyOrder("NOT_FOUND_PROVIDER", HttpStatus.NOT_FOUND, "provider를 찾을 수 없습니다.");
	}

	@DisplayName("잘못된 인가 코드로 소셜 로그인하여 회원가입을 할 수 없다")
	@Test
	void signUpWithInvalidCode() throws IOException {
		// given
		List<Long> addressIds = getAddressIds(regionRepository.findAllByNameIn(List.of("서울 송파구 가락동")));
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("loginId", "23Yong");
		requestBody.put("addressIds", addressIds);
		OauthSignUpRequest request = objectMapper.readValue(objectMapper.writeValueAsString(requestBody),
			OauthSignUpRequest.class);

		Map<String, Object> responseBody = new HashMap<>();
		responseBody.put("accessToken", "accessTokenValue");
		responseBody.put("scope", "scopeValue");
		responseBody.put("tokenType", "Bearer");
		OauthAccessTokenResponse mockAccessTokenResponse =
			objectMapper.readValue(objectMapper.writeValueAsString(responseBody), OauthAccessTokenResponse.class);

		given(oauthClientRepository.findOneBy(anyString())).willReturn(oauthClient);
		given(oauthClient.exchangeAccessTokenByAuthorizationCode(anyString(), anyString()))
			.willReturn(mockAccessTokenResponse);
		given(oauthClient.getUserProfileByAccessToken(any(OauthAccessTokenResponse.class)))
			.willThrow(new BadRequestException(OauthErrorCode.WRONG_AUTHORIZATION_CODE));

		String provider = "naver";
		String code = "1234";
		// when
		Throwable throwable = catchThrowable(
			() -> oauthService.signUp(createMultipartFile("cat.png"), request, provider, code,
				"http://localhost:5173/my-account/oauth"));

		// then
		assertThat(throwable)
			.isInstanceOf(BadRequestException.class)
			.extracting("errorCode")
			.extracting("name", "httpStatus", "message")
			.containsExactlyInAnyOrder("WRONG_AUTHORIZATION_CODE", HttpStatus.BAD_REQUEST, "잘못된 인가 코드입니다.");
	}

	@DisplayName("로그인 아이디가 중복되는 경우 회원가입을 할 수 없다")
	@Test
	void signUpWhenDuplicateLoginId() throws IOException {
		// given
		regionRepository.save(createRegion("서울 송파구 가락동"));
		Member member = memberRepository.save(createMember("avatarUrlValue", "23Yong1234@gmail.com", "23Yong"));

		Region region = regionRepository.findAllByNameIn(List.of("서울 송파구 가락동")).stream().findAny().orElseThrow();

		MemberTown memberTown = MemberTown.selectedMemberTown(region, member);
		memberTownRepository.save(memberTown);

		String provider = "naver";
		String code = "1234";
		MockMultipartFile profile = createMultipartFile("cat.png");

		List<Long> addressIds = getAddressIds(regionRepository.findAllByNameIn(List.of("서울 송파구 가락동")));
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("loginId", "23Yong");
		requestBody.put("addressIds", addressIds);
		OauthSignUpRequest request = objectMapper.readValue(objectMapper.writeValueAsString(requestBody),
			OauthSignUpRequest.class);

		Map<String, Object> responseBody = new HashMap<>();
		responseBody.put("accessToken", "accessTokenValue");
		responseBody.put("scope", "scopeValue");
		responseBody.put("tokenType", "Bearer");
		OauthAccessTokenResponse mockAccessTokenResponse =
			objectMapper.readValue(objectMapper.writeValueAsString(responseBody), OauthAccessTokenResponse.class);

		Map<String, Object> userProfileResponseBody = new HashMap<>();
		userProfileResponseBody.put("email", "23Yong@gmail.com");
		userProfileResponseBody.put("profileImage", "profileImageValue");
		OauthUserProfileResponse mockUserProfileResponse = objectMapper.readValue(
			objectMapper.writeValueAsString(userProfileResponseBody), OauthUserProfileResponse.class);

		given(oauthClientRepository.findOneBy(anyString()))
			.willReturn(oauthClient);
		given(oauthClient.exchangeAccessTokenByAuthorizationCode(anyString(), anyString()))
			.willReturn(mockAccessTokenResponse);
		given(oauthClient.getUserProfileByAccessToken(any(OauthAccessTokenResponse.class)))
			.willReturn(mockUserProfileResponse);

		// when
		Throwable throwable = catchThrowable(() -> oauthService.signUp(profile, request, provider, code, null));

		// then
		assertThat(throwable)
			.isInstanceOf(ConflictException.class)
			.extracting("errorCode.message")
			.isEqualTo("중복된 아이디입니다.");
	}

	@DisplayName("로그인 아이디와 인가코드를 가지고 소셜 로그인을 한다")
	@Test
	void login() throws JsonProcessingException {
		// given
		Member member = new Member("avatarUrlValue", "23Yong@gmail.com", "23Yong");
		memberRepository.save(member);

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("loginId", "23Yong");
		OauthLoginRequest request = objectMapper.readValue(objectMapper.writeValueAsString(requestBody),
			OauthLoginRequest.class);
		String provider = "naver";
		String code = "1234";

		Map<String, Object> responseBody = new HashMap<>();
		responseBody.put("accessToken", "accessTokenValue");
		responseBody.put("scope", "scopeValue");
		responseBody.put("tokenType", "Bearer");
		OauthAccessTokenResponse mockAccessTokenResponse =
			objectMapper.readValue(objectMapper.writeValueAsString(responseBody), OauthAccessTokenResponse.class);

		Map<String, Object> userProfileResponseBody = new HashMap<>();
		userProfileResponseBody.put("email", "23Yong@gmail.com");
		userProfileResponseBody.put("profileImage", "profileImageValue");
		OauthUserProfileResponse mockUserProfileResponse = objectMapper.readValue(
			objectMapper.writeValueAsString(userProfileResponseBody), OauthUserProfileResponse.class);

		LocalDateTime now = createNow();
		// mocking
		given(oauthClientRepository.findOneBy(anyString())).willReturn(oauthClient);
		given(oauthClient.exchangeAccessTokenByAuthorizationCode(anyString(), anyString()))
			.willReturn(mockAccessTokenResponse);
		given(oauthClient.getUserProfileByAccessToken(any(OauthAccessTokenResponse.class)))
			.willReturn(mockUserProfileResponse);

		// when
		OauthLoginResponse response = oauthService.login(request, provider, code, now,
			"http://localhost:5173/my-account/oauth");

		// then
		assertThat(response)
			.extracting("jwt.accessToken", "jwt.refreshToken", "user.loginId", "user.profileUrl")
			.contains(
				createExpectedAccessTokenBy(jwtProvider, member, now),
				createExpectedRefreshTokenBy(jwtProvider, member, now),
				"23Yong",
				"avatarUrlValue");
	}

	@DisplayName("로그아웃을 수행한다")
	@Test
	void logout() throws JsonProcessingException {
		// given
		Member member = createMember("avatarUrlValue", "23Yong@gmail.com", "23Yong");
		LocalDateTime now = createNow();
		Jwt jwt = jwtProvider.createJwtBasedOnMember(member, now);

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("refreshToken", jwt.getRefreshToken());
		OauthLogoutRequest request = objectMapper.readValue(objectMapper.writeValueAsString(requestBody),
			OauthLogoutRequest.class);

		// when
		oauthService.logout(jwt.getAccessToken(), request);

		// then
		assertThat(redisService.get(jwt.getAccessToken())).isEqualTo("logout");
	}

	@DisplayName("만료된 액세스 토큰을 가지고 로그아웃을 요청하면 블랙리스트에 추가하지 않는다")
	@Test
	void logoutWithExpireAccessToken() throws JsonProcessingException {
		// given
		Member member = createMember("avatarUrlValue", "23Yong@gmail.com", "23Yong");
		LocalDateTime now = LocalDateTime.now().minusDays(1);
		Jwt jwt = jwtProvider.createJwtBasedOnMember(member, now);

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("refreshToken", jwt.getRefreshToken());
		OauthLogoutRequest request = objectMapper.readValue(objectMapper.writeValueAsString(requestBody),
			OauthLogoutRequest.class);

		// when
		oauthService.logout(jwt.getAccessToken(), request);

		// then
		assertThat(redisService.get(jwt.getAccessToken())).isNull();
	}

	@DisplayName("만료된 리프레쉬 토큰을 가지고 로그아웃을 요청하면 이미 만료되어 아무 처리도 하지 않는다")
	@Test
	void logoutWithExpireRefreshToken() throws JsonProcessingException {
		// given
		Member member = createMember("avatarUrlValue", "23Yong@gmail.com", "23Yong");
		LocalDateTime now = createNow().minusHours(10);
		Jwt jwt = jwtProvider.createJwtBasedOnMember(member, now);

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("refreshToken", jwt.getRefreshToken());
		OauthLogoutRequest request = objectMapper.readValue(objectMapper.writeValueAsString(requestBody),
			OauthLogoutRequest.class);

		redisService.saveRefreshToken(member.createRedisKey(), jwt);
		// when
		oauthService.logout(jwt.getAccessToken(), request);

		// then
		assertThat(redisService.get(member.createRedisKey())).isNull();
	}

	@DisplayName("리프레쉬 토큰을 가지고 액세스 토큰을 갱신한다")
	@Test
	void refreshAccessToken() throws JsonProcessingException {
		// given
		Member member = createMember("avatarUrlValue", "23Yong@gmail.com", "23Yong");
		LocalDateTime now = createNow();

		Jwt jwt = jwtProvider.createJwtBasedOnMember(member, now);

		redisService.saveRefreshToken(member.createRedisKey(), jwt);
		memberRepository.save(member);

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("refreshToken", jwt.getRefreshToken());

		OauthRefreshRequest request = objectMapper.readValue(objectMapper.writeValueAsString(requestBody),
			OauthRefreshRequest.class);

		// when
		OauthRefreshResponse response = oauthService.refreshAccessToken(request, now);

		// then
		assertThat(response)
			.extracting("accessToken")
			.isEqualTo(createExpectedAccessTokenBy(jwtProvider, member, now));
	}

	@DisplayName("유효하지 않은 토큰으로는 액세스 토큰을 갱신할 수 없다")
	@Test
	void refreshAccessTokenWithInvalidRefreshToken() throws JsonProcessingException {
		// given
		Member member = createMember("avatarUrlValue", "23Yong@gmail.com", "23Yong");
		LocalDateTime now = createNow();

		Jwt jwt = jwtProvider.createJwtBasedOnMember(member, now);

		redisService.saveRefreshToken(member.createRedisKey(), jwt);
		memberRepository.save(member);

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("refreshToken", "invalidRefreshTokenValue");

		OauthRefreshRequest request = objectMapper.readValue(objectMapper.writeValueAsString(requestBody),
			OauthRefreshRequest.class);

		// when
		Throwable throwable = catchThrowable(() -> oauthService.refreshAccessToken(request, now));

		// then
		assertThat(throwable)
			.isInstanceOf(BadRequestException.class)
			.extracting("errorCode.message")
			.isEqualTo("유효하지 않은 토큰입니다.");
	}

	protected List<Long> getAddressIds(List<Region> regions) {
		return regions.stream()
			.map(Region::getId)
			.collect(Collectors.toUnmodifiableList());
	}
}
