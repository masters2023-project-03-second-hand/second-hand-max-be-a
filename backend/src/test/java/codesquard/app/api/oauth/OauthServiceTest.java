package codesquard.app.api.oauth;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import codesquard.app.IntegrationTestSupport;

class OauthServiceTest extends IntegrationTestSupport {

	@Autowired
	private OauthService oauthService;

	@DisplayName("로그인 아이디와 인가 코드를 가지고 소셜 로그인을 한다")
	@Test
	public void login() {
		// given
		OauthLoginRequest request = new OauthLoginRequest("23Yong");
		String code = "1234";
		// when
		OauthLoginResponse response = oauthService.login(request, code);
		// then
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(response).extracting("user").extracting("loginId").isEqualTo("23Yong");
			softAssertions.assertThat(response).extracting("user").extracting("profileUrl").isEqualTo("");
			softAssertions.assertThat(response)
				.extracting("jwt")
				.extracting("accessToken")
				.isEqualTo("accessTokenValue");
			softAssertions.assertThat(response)
				.extracting("jwt")
				.extracting("refreshToken")
				.isEqualTo("refreshTokenValue");
			softAssertions.assertAll();
		});
	}

}