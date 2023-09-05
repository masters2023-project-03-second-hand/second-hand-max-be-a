package codesquard.app.api.oauth.response;

import codesquard.app.domain.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OauthLoginMemberResponse {
	private String loginId;
	private String profileUrl;

	private OauthLoginMemberResponse(String loginId, String profileUrl) {
		this.loginId = loginId;
		this.profileUrl = profileUrl;
	}

	public static OauthLoginMemberResponse from(Member member) {
		return new OauthLoginMemberResponse(member.getLoginId(), member.getAvatarUrl());
	}

	@Override
	public String toString() {
		return String.format("%s, %s(loginId=%s)", "로그인 회원정보 응답", this.getClass().getSimpleName(), loginId);
	}
}
