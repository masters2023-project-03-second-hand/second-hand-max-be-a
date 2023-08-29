package codesquard.app.domain.member;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuthenticateMemberTest {

	@DisplayName("Member 엔티티를 가지고 AuthenticateUser 객체를 생성한다")
	@Test
	public void from() {
		// given
		Member member = Member.create("avatarUrlValue", "23Yong1234@gmail.com", "23Yong");
		// when
		AuthenticateMember authMember = AuthenticateMember.from(member);
		// then
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(authMember)
				.extracting("loginId", "profileUrl")
				.contains("23Yong", "avatarUrlValue");
			softAssertions.assertAll();
		});
	}

}
