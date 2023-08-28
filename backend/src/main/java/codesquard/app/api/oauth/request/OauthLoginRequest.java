package codesquard.app.api.oauth.request;

import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class OauthLoginRequest {
	@Pattern(regexp = "^[a-zA-Z0-9]{2,12}$", message = "아이디는 띄어쓰기 없이 영문, 숫자로 구성되며 2~12글자로 구성되어야 합니다.")
	private String loginId;

	public OauthLoginRequest(String loginId) {
		this.loginId = loginId;
	}
}
