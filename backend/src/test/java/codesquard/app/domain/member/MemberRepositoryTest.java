package codesquard.app.domain.member;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import codesquard.app.IntegrationTestSupport;

class MemberRepositoryTest extends IntegrationTestSupport {

	@BeforeEach
	void cleanup() {
		memberTownRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
	}

	@DisplayName("로그인 아이디를 가지고 회원을 조회할 수 있다")
	@Test
	public void findMemberByLoginId() {
		// given
		Member member = Member.create("avatarUrl", "23Yong@gmail.com", "23Yong");
		memberRepository.save(member);
		String loginId = "23Yong";

		// when
		Member findMember = memberRepository.findMemberByLoginId(loginId);

		// then
		Assertions.assertThat(member.getLoginId()).isEqualTo(findMember.getLoginId());
	}

	@DisplayName("로그인 아이디를 가지고 회원을 조회할때 해당하는 회원이 없는 경우 null을 반환한다")
	@Test
	public void findMemberByLoginIdWhenMemberIsNotExist() {
		// given
		String loginId = "23Yong";

		// when
		Member findMember = memberRepository.findMemberByLoginId(loginId);

		// then
		Assertions.assertThat(findMember).isNull();
	}

	@DisplayName("닉네임을 가지고 회원이 존재하는지 확인한다")
	@Test
	public void existsMemberByLoginId() {
		// given
		Member member = Member.create("avatarUrl", "23Yong@gmail.com", "23Yong");
		memberRepository.save(member);
		String loginId = "23Yong";

		// when
		boolean result = memberRepository.existsMemberByLoginId(loginId);

		// then
		Assertions.assertThat(result).isTrue();
	}
}
