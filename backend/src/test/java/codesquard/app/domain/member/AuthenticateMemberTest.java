package codesquard.app.domain.member;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class AuthenticateMemberTest {

	@DisplayName("Member 엔티티를 가지고 AuthenticateUser 객체를 생성한다")
	@Test
	public void from() {
		// given
		Member member = new Member("avatarUrlValue", "23Yong1234@gmail.com", "23Yong");

		// when
		AuthenticateMember authMember = AuthenticateMember.from(member);

		// then
		assertThat(authMember)
			.extracting("loginId", "profileUrl")
			.contains("23Yong", "avatarUrlValue");
	}

}
