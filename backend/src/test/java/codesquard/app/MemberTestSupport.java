package codesquard.app;

import codesquard.app.domain.member.Member;

public class MemberTestSupport {

	public static Member createMember(String avatarUrl, String email, String loginId) {
		return new Member(avatarUrl, email, loginId);
	}
}
