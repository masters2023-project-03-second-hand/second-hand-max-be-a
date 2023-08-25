package codesquard.app.api.oauth;

import javax.validation.constraints.Pattern;

public class OauthLoginRequest {
	@Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,12}$", message = "아이디는 띄어쓰기 없이 한글, 영문, 숫자로 구성되며 2~12글자로 구성되어야 합니다.")
	private String loginId;

	public OauthLoginRequest(String loginId) {
		this.loginId = loginId;
	}
}
