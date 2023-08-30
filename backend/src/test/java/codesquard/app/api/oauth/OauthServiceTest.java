package codesquard.app.api.oauth;

import static codesquard.app.api.oauth.OauthFixedFactory.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.IntegrationTestSupport;
import codesquard.app.api.errors.errorcode.MemberErrorCode;
import codesquard.app.api.errors.errorcode.OauthErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.oauth.request.OauthLoginRequest;
import codesquard.app.api.oauth.request.OauthLogoutRequest;
import codesquard.app.api.oauth.request.OauthSignUpRequest;
import codesquard.app.api.oauth.response.OauthAccessTokenResponse;
import codesquard.app.api.oauth.response.OauthLoginResponse;
import codesquard.app.api.oauth.response.OauthLogoutResponse;
import codesquard.app.api.oauth.response.OauthSignUpResponse;
import codesquard.app.api.oauth.response.OauthUserProfileResponse;
import codesquard.app.domain.jwt.JwtProperties;
import codesquard.app.domain.jwt.JwtProvider;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.membertown.MemberTown;
import codesquard.app.domain.oauth.client.OauthClient;
import codesquard.app.domain.oauth.repository.OauthClientRepository;
import codesquard.app.domain.oauth.support.Principal;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Transactional
class OauthServiceTest extends IntegrationTestSupport {

	private static final Logger log = LoggerFactory.getLogger(OauthServiceTest.class);

	@MockBean
	private OauthClientRepository oauthClientRepository;

	@Mock
	private OauthClient oauthClient;

	@Autowired
	private JwtProperties jwtProperties;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private JwtProvider jwtProvider;

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

		// when
		OauthSignUpResponse response = oauthService.signUp(profile, request, provider, code);

		// then
		Member findMember = memberRepository.findMemberByLoginId("23Yong")
			.orElseThrow(() -> new RestApiException(MemberErrorCode.NOT_FOUND_MEMBER));

		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(response)
				.extracting("email", "loginId")
				.containsExactlyInAnyOrder("23Yong@gmail.com",
					"23Yong");
			softAssertions.assertThat(findMember)
				.extracting("email", "loginId")
				.containsExactlyInAnyOrder("23Yong@gmail.com",
					"23Yong");
			softAssertions.assertThat(findMember.getTowns())
				.hasSize(1)
				.extracting("name")
				.containsExactlyInAnyOrder("가락 1동");
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
		String avatarUrl = "avatarUrlValue";
		String loginId = "23Yong";
		String email = "23Yong@gmail.com";
		Member member = Member.create(avatarUrl, email, loginId);
		memberRepository.save(member);

		OauthLoginRequest request = OauthFixedFactory.createFixedOauthLoginRequest();
		String provider = "naver";
		String code = "1234";
		OauthAccessTokenResponse mockAccessTokenResponse = createFixedOauthAccessTokenResponse();
		OauthUserProfileResponse mockUserProfileResponse = createOauthUserProfileResponse();

		// mocking
		when(oauthClientRepository.findOneBy(anyString())).thenReturn(oauthClient);
		when(oauthClient.exchangeAccessTokenByAuthorizationCode(anyString()))
			.thenReturn(mockAccessTokenResponse);
		when(oauthClient.getUserProfileByAccessToken(any(OauthAccessTokenResponse.class)))
			.thenReturn(mockUserProfileResponse);

		// when
		OauthLoginResponse response = oauthService.login(request, provider, code);
		log.debug("response : {}", response);

		// then
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(response)
				.extracting("user.loginId", "user.profileUrl")
				.contains("23Yong", "avatarUrlValue");
			softAssertions.assertAll();
		});
	}

	@DisplayName("로그아웃을 수행한다")
	@Test
	public void logout() {
		// given
		String avatarUrl = "avatarUrlValue";
		String loginId = "23Yong";
		String email = "23Yong@gmail.com";
		Member member = Member.create(avatarUrl, email, loginId);
		Member saveMember = memberRepository.save(member);

		Map<String, Object> claims = member.createClaims();
		Date expireDateAccessToken = jwtProperties.getExpireDateAccessToken();
		String accessToken = createToken(claims, expireDateAccessToken);
		Principal principal = jwtProvider.extractPrincipal(accessToken);
		OauthLogoutRequest request = OauthLogoutRequest.create(principal);

		// when
		OauthLogoutResponse response = oauthService.logout(request);
		log.debug("response : {}", response);

		// then
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(response)
				.extracting("id", "email")
				.contains(saveMember.getId(), "23Yong@gmail.com");
			softAssertions.assertThat(redisTemplate.opsForValue().get(principal.getAccessToken())).isEqualTo("logout");
			softAssertions.assertAll();
		});
	}

	private String createToken(Map<String, Object> claims, Date expireDate) {
		// claims를 비밀키로 이용하여 암호화
		return Jwts.builder()
			.setClaims(claims)
			.setExpiration(expireDate)
			.signWith(jwtProperties.getKey(), SignatureAlgorithm.HS256)
			.compact();
	}
}
