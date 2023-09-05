package codesquard.app.api.oauth.response;

import codesquard.app.domain.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OauthSignUpResponse {

	private Long id;

	private String avatarUrl;

	private String email;

	private String loginId;

	private OauthSignUpResponse(Long id, String avatarUrl, String email, String loginId) {
		this.id = id;
		this.avatarUrl = avatarUrl;
		this.email = email;
		this.loginId = loginId;
	}

	public static OauthSignUpResponse from(Member member) {
		return new OauthSignUpResponse(member.getId(), member.getAvatarUrl(), member.getEmail(),
			member.getLoginId());
	}

	public static OauthSignUpResponse create(Long id, String avatarUrl, String email, String loginId) {
		return new OauthSignUpResponse(id, avatarUrl, email, loginId);
	}

	@Override
	public String toString() {
		return String.format("%s, %s(loginId=%s)", "회원가입 응답", this.getClass().getSimpleName(), loginId);
	}
}
