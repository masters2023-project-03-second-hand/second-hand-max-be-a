package codesquard.app.api.oauth.response;

import codesquard.app.domain.member.Member;
import lombok.Getter;

@Getter
public class OauthSignUpResponse {
	private final Long id;

	private final String avatarUrl;

	private final String email;

	private final String loginId;

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
