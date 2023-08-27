package codesquard.app.api.oauth.response;

import codesquard.app.domain.member.Member;
import lombok.Getter;

@Getter
public class OauthSignUpResponse {
	private final Long id;

	private final String avatarUrl;

	private final String socialLoginId;

	private final String nickname;

	public OauthSignUpResponse(Long id, String avatarUrl, String socialLoginId, String nickname) {
		this.id = id;
		this.avatarUrl = avatarUrl;
		this.socialLoginId = socialLoginId;
		this.nickname = nickname;
	}

	public static OauthSignUpResponse of(Member member) {
		return new OauthSignUpResponse(member.getId(), member.getAvatarUrl(), member.getSocialLoginId(),
			member.getNickname());
	}
}
