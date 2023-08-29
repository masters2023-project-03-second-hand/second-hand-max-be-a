package codesquard.app.api.oauth.response;

import codesquard.app.domain.member.AuthenticateMember;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class OauthLogoutResponse {
	private Long id;
	private String email;
	private String loginId;
	private String profileUrl;

	private OauthLogoutResponse() {

	}

	public OauthLogoutResponse(Long id, String email, String loginId, String profileUrl) {
		this.id = id;
		this.email = email;
		this.loginId = loginId;
		this.profileUrl = profileUrl;
	}

	public static OauthLogoutResponse from(AuthenticateMember authMember) {
		return new OauthLogoutResponse(authMember.getId(), authMember.getEmail(), authMember.getLoginId(),
			authMember.getProfileUrl());
	}
}
