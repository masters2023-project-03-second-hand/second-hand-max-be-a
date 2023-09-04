package codesquard.app.domain.member;

import lombok.Getter;

@Getter
public class AuthenticateMember {

	private Long id;
	private String email;
	private String loginId;
	private String profileUrl;

	private AuthenticateMember() {

	}

	private AuthenticateMember(Long id, String email, String loginId, String profileUrl) {
		this.id = id;
		this.email = email;
		this.loginId = loginId;
		this.profileUrl = profileUrl;
	}

	public static AuthenticateMember from(Member member) {
		return new AuthenticateMember(member.getId(), member.getEmail(), member.getLoginId(), member.getAvatarUrl());
	}

	public String createRedisKey() {
		return "RT:" + email;
	}

	@Override
	public String toString() {
		return String.format("%s, %s(id=%d, loginId=%s)", "인증 회원", this.getClass().getSimpleName(), id, loginId);
	}
}
