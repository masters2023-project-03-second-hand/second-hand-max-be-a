package codesquard.app.api.oauth;

import static org.mockito.Mockito.*;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
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
import codesquard.app.domain.oauth.OauthProvider;

@Transactional
class OauthServiceTest extends IntegrationTestSupport {

	@MockBean
	private OauthClient oauthClient;

	@DisplayName("로그인 아이디와 소셜 로그인을 하여 회원가입을 한다")
	@Test
	public void signUp() {
		// given
		String provider = "naver";
		String code = "1234";
		OauthSignUpRequest request = OauthSignUpRequest.create("23Yong", "가락 1동");
		OauthAccessTokenResponse mockAccessTokenResponse = OauthAccessTokenResponse.create(
			"accessTokenValue", "scopeValue", "Bearer");
		OauthUserProfileResponse mockUserProfileResponse = OauthUserProfileResponse.create("23Yong1234");
		// mocking
		when(oauthClient.exchangeAccessTokenByAuthorizationCode(
			any(OauthProvider.class), anyString()))
			.thenReturn(mockAccessTokenResponse);
		when(oauthClient.getUserProfileByAccessToken(
			anyString(), any(OauthProvider.class), any(OauthAccessTokenResponse.class)))
			.thenReturn(mockUserProfileResponse);
		// when
		OauthSignUpResponse response = oauthService.signUp(request, provider, code);
		// then
		Member findMember = memberRepository.findMemberByNicknameIs("23Yong");

		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(response)
				.extracting("avatarUrl", "socialLoginId", "nickname")
				.containsExactlyInAnyOrder(null, "23Yong1234", "23Yong");
			softAssertions.assertThat(findMember)
				.extracting("avatarUrl", "socialLoginId", "nickname")
				.containsExactlyInAnyOrder(null, "23Yong1234", "23Yong");
			softAssertions.assertThat(findMember.getTowns())
				.hasSize(1)
				.extracting("name")
				.containsExactlyInAnyOrder("가락 1동");
			softAssertions.assertAll();
		});
	}

	@DisplayName("제공되지 않은 provider로 소셜 로그인하여 회원가입을 할 수 없다")
	@Test
	public void signUpWithInvalidProvider() {
		// given
		String provider = "github";
		String code = "1234";
		OauthSignUpRequest request = OauthSignUpRequest.create("23Yong", "가락 1동");
		// when
		Throwable throwable = Assertions.catchThrowable(() -> oauthService.signUp(request, provider, code));
		// then
		Assertions.assertThat(throwable)
			.isInstanceOf(RestApiException.class)
			.extracting("errorCode")
			.extracting("name", "httpStatus", "message")
			.containsExactlyInAnyOrder("NOT_FOUND_PROVIDER", HttpStatus.NOT_FOUND, "provider를 찾을 수 없습니다.");
	}

	@DisplayName("잘못된 인가 코드로 소셜 로그인하여 회원가입을 할 수 없다")
	@Test
	public void signUpWithInvalidCode() {
		// given
		String provider = "naver";
		String code = "1234";
		OauthSignUpRequest request = OauthSignUpRequest.create("23Yong", "가락 1동");
		OauthAccessTokenResponse mockAccessTokenResponse = OauthAccessTokenResponse.create(
			"accessTokenValue", "scopeValue", "Bearer");
		// mocking
		when(oauthClient.exchangeAccessTokenByAuthorizationCode(
			any(OauthProvider.class), anyString()))
			.thenReturn(mockAccessTokenResponse);
		when(oauthClient.getUserProfileByAccessToken(anyString(), any(OauthProvider.class),
			any(OauthAccessTokenResponse.class)))
			.thenThrow(new RestApiException(OauthErrorCode.WRONG_AUTHORIZATION_CODE));
		// when
		Throwable throwable = Assertions.catchThrowable(() -> oauthService.signUp(request, provider, code));
		// then
		Assertions.assertThat(throwable)
			.isInstanceOf(RestApiException.class)
			.extracting("errorCode")
			.extracting("name", "httpStatus", "message")
			.containsExactlyInAnyOrder("WRONG_AUTHORIZATION_CODE", HttpStatus.BAD_REQUEST, "잘못된 인가 코드입니다.");
	}
}