package codesquard.app.api.oauth.response;

import codesquard.app.domain.member.Member;
import lombok.Getter;

@Getter
public class OauthLoginMemberResponse {
	private String loginId;
	private String profileUrl;

	private OauthLoginMemberResponse() {

	}

	private OauthLoginMemberResponse(String loginId, String profileUrl) {
		this.loginId = loginId;
		this.profileUrl = profileUrl;
	}

	public static OauthLoginMemberResponse from(Member member) {
		return new OauthLoginMemberResponse(member.getLoginId(), member.getAvatarUrl());
	}
}
