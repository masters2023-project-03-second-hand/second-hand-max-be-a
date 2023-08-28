package codesquard.app.api.oauth;

import static codesquard.app.api.oauth.OauthFixedFactory.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.IntegrationTestSupport;
import codesquard.app.api.errors.errorcode.OauthErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.oauth.client.OauthClient;
import codesquard.app.api.oauth.request.OauthSignUpRequest;
import codesquard.app.api.oauth.response.OauthAccessTokenResponse;
import codesquard.app.api.oauth.response.OauthSignUpResponse;
import codesquard.app.api.oauth.response.OauthUserProfileResponse;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.membertown.MemberTown;
import codesquard.app.domain.oauth.OauthProvider;

@Transactional
class OauthServiceTest extends IntegrationTestSupport {

	@MockBean
	private OauthClient oauthClient;

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
		when(oauthClient.exchangeAccessTokenByAuthorizationCode(
			any(OauthProvider.class), anyString()))
			.thenReturn(mockAccessTokenResponse);
		when(oauthClient.getUserProfileByAccessToken(
			anyString(), any(OauthProvider.class), any(OauthAccessTokenResponse.class)))
			.thenReturn(mockUserProfileResponse);
		// when
		OauthSignUpResponse response = oauthService.signUp(profile, request, provider, code);
		// then
		Member findMember = memberRepository.findMemberByLoginIdIs("23Yong");

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
		// when
		Throwable throwable = Assertions.catchThrowable(() -> oauthService.signUp(profile, request, provider, code));
		// then
		Assertions.assertThat(throwable)
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
		when(oauthClient.exchangeAccessTokenByAuthorizationCode(
			any(OauthProvider.class), anyString()))
			.thenReturn(mockAccessTokenResponse);
		when(oauthClient.getUserProfileByAccessToken(anyString(), any(OauthProvider.class),
			any(OauthAccessTokenResponse.class)))
			.thenThrow(new RestApiException(OauthErrorCode.WRONG_AUTHORIZATION_CODE));
		// when
		Throwable throwable = Assertions.catchThrowable(() -> oauthService.signUp(profile, request, provider, code));
		// then
		Assertions.assertThat(throwable)
			.isInstanceOf(RestApiException.class)
			.extracting("errorCode")
			.extracting("name", "httpStatus", "message")
			.containsExactlyInAnyOrder("WRONG_AUTHORIZATION_CODE", HttpStatus.BAD_REQUEST, "잘못된 인가 코드입니다.");
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
		when(oauthClient.exchangeAccessTokenByAuthorizationCode(
			any(OauthProvider.class), anyString()))
			.thenReturn(mockAccessTokenResponse);
		when(oauthClient.getUserProfileByAccessToken(
			anyString(), any(OauthProvider.class), any(OauthAccessTokenResponse.class)))
			.thenReturn(mockUserProfileResponse);
		// when // then
		Assertions.assertThatThrownBy(() -> oauthService.signUp(profile, request, provider, code))
			.isInstanceOf(RestApiException.class)
			.extracting("errorCode.message")
			.isEqualTo("이미 존재하는 아이디입니다.");
	}
}
