package codesquard.app.domain.member;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.IntegrationTestSupport;
import codesquard.app.domain.membertown.MemberTown;

@Transactional
class MemberTest extends IntegrationTestSupport {

	@Autowired
	private MemberRepository memberRepository;

	@DisplayName("회원의 동네를 추가한다")
	@Test
	public void addMemberTown() {
		// given
		Member member = Member.create(null, "23Yong1234@gmail.com", "23Yong");
		MemberTown town = MemberTown.create("가락 1동");

		// when
		member.addMemberTown(town);

		// then
		Member saveMember = memberRepository.save(member);
		assertThat(saveMember.getTowns())
			.hasSize(1)
			.extracting("name")
			.containsExactlyInAnyOrder("가락 1동");
	}
}
