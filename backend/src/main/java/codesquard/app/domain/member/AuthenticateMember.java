package codesquard.app.domain.member;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
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
}
