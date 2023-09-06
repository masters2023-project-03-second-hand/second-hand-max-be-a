package codesquard.app.api.oauth.request;

import static codesquard.app.config.ValidationGroups.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OauthLoginRequest {

	@NotBlank(message = "로그인 아이디는 필수 정보입니다.", groups = NotBlankGroup.class)
	@Pattern(regexp = "^[a-zA-Z0-9]{2,12}$", message = "아이디는 띄어쓰기 없이 영문, 숫자로 구성되며 2~12글자로 구성되어야 합니다.", groups = PatternGroup.class)
	private String loginId;

	private OauthLoginRequest(String loginId) {
		this.loginId = loginId;
	}

	public static OauthLoginRequest create(String loginId) {
		return new OauthLoginRequest(loginId);
	}

	@Override
	public String toString() {
		return String.format("%s, %s(loginId=%s)", "로그인 요청", this.getClass().getSimpleName(), loginId);
	}
}
