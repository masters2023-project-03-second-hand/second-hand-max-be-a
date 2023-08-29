package codesquard.app.domain.member;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AuthenticateMember {
	@JsonIgnore
	private String email;

	private String loginId;

	private String profileUrl;

	private AuthenticateMember() {

	}

	private AuthenticateMember(String email, String loginId, String profileUrl) {
		this.email = email;
		this.loginId = loginId;
		this.profileUrl = profileUrl;
	}

	public static AuthenticateMember from(Member member) {
		return new AuthenticateMember(member.getEmail(), member.getLoginId(), member.getAvatarUrl());
	}

	public String createRedisKey() {
		return "RT:" + email;
	}
}
