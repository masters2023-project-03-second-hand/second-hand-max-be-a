package codesquard.app.domain.member;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.IntegrationTestSupport;
import codesquard.app.domain.membertown.MemberTown;

@Transactional
class MemberTest extends IntegrationTestSupport {

	private static final Logger log = LoggerFactory.getLogger(MemberTest.class);

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
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(saveMember.getTowns().get(0).getName()).isEqualTo("가락 1동");
			softAssertions.assertAll();
		});
	}
}
