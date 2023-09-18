package codesquard.app.api.oauth.response;

import codesquard.app.domain.member.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OauthSignUpResponse {

	private Long id;

	private String avatarUrl;

	private String email;

	private String loginId;

	public static OauthSignUpResponse from(Member member) {
		return new OauthSignUpResponse(member.getId(), member.getAvatarUrl(), member.getEmail(),
			member.getLoginId());
	}

	@Override
	public String toString() {
		return String.format("%s, %s(loginId=%s)", "회원가입 응답", this.getClass().getSimpleName(), loginId);
	}
}
