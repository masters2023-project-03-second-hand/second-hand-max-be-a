package codesquard.app.api.oauth.response;

import codesquard.app.domain.member.Member;
import lombok.Getter;

@Getter
public class OauthSignUpResponse {
	private final Long id;

	private final String avatarUrl;

	private final String socialLoginId;

	private final String nickname;

	private OauthSignUpResponse(Long id, String avatarUrl, String socialLoginId, String nickname) {
		this.id = id;
		this.avatarUrl = avatarUrl;
		this.socialLoginId = socialLoginId;
		this.nickname = nickname;
	}

	public static OauthSignUpResponse from(Member member) {
		return new OauthSignUpResponse(member.getId(), member.getAvatarUrl(), member.getSocialLoginId(),
			member.getNickname());
	}

	public static OauthSignUpResponse create(Long id, String avatarUrl, String socialLoginId, String nickname) {
		return new OauthSignUpResponse(id, avatarUrl, socialLoginId, nickname);
	}
}
