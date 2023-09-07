package codesquard.app.domain.member;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import codesquard.app.IntegrationTestSupport;
import codesquard.app.api.errors.errorcode.MemberErrorCode;
import codesquard.app.api.errors.errorcode.OauthErrorCode;
import codesquard.app.api.errors.exception.RestApiException;

class MemberRepositoryTest extends IntegrationTestSupport {

	@BeforeEach
	void cleanup() {
		chatLogRepository.deleteAllInBatch();
		chatRoomRepository.deleteAllInBatch();
		interestRepository.deleteAllInBatch();
		imageRepository.deleteAllInBatch();
		itemRepository.deleteAllInBatch();
		categoryRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
		memberTownRepository.deleteAllInBatch();
	}

	@DisplayName("로그인 아이디를 가지고 회원을 조회할 수 있다")
	@Test
	public void findMemberByLoginId() {
		// given
		Member member = Member.create("avatarUrl", "23Yong@gmail.com", "23Yong");
		memberRepository.save(member);
		String loginId = "23Yong";

		// when
		Member findMember = memberRepository.findMemberByLoginId(loginId)
			.orElseThrow(() -> new RestApiException(MemberErrorCode.NOT_FOUND_MEMBER));

		// then
		Assertions.assertThat(member.getLoginId()).isEqualTo(findMember.getLoginId());
	}

	@DisplayName("로그인 아이디를 가지고 회원을 조회할때 해당하는 회원이 없는 경우 null을 반환한다")
	@Test
	public void findMemberByLoginIdWhenMemberIsNotExist() {
		// given
		String loginId = "23Yong";

		// when
		Member findMember = memberRepository.findMemberByLoginId(loginId)
			.orElse(null);

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

	@DisplayName("로그인 아이디와 이메일을 가지고 회원을 조회한다")
	@Test
	public void findMemberByLoginIdAndAndEmail() {
		// given
		String loginId = "23Yong";
		String email = "23Yong1234@gmail.com";
		Member member = Member.create(null, email, loginId);
		memberRepository.save(member);
		// when
		Member findMember = memberRepository.findMemberByLoginIdAndEmail(loginId, email)
			.orElseThrow(() -> new RestApiException(OauthErrorCode.FAIL_LOGIN));
		// then
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(findMember)
				.extracting("loginId", "email")
				.contains(loginId, email);
			softAssertions.assertAll();
		});
	}

	@DisplayName("로그인 아이디와 이메일을 가지고 회원을 조회할때 회원이 없는 경우 null을 반환한다")
	@Test
	public void findMemberByLoginIdAndAndEmailWhenMemberIsNotExist() {
		// given
		String loginId = "23Yong";
		String email = "23Yong1234@gmail.com";
		// when
		Member findMember = memberRepository.findMemberByLoginIdAndEmail(loginId, email)
			.orElse(null);
		// then
		Assertions.assertThat(findMember).isNull();
	}

	@DisplayName("이메일을 가지고 회원을 조회한다")
	@Test
	public void findMemberByEmail() {
		// given
		String email = "23Yong@gmail.com";
		String loginId = "23Yong";
		Member member = Member.create(null, email, loginId);
		memberRepository.save(member);
		// when
		Member findMember = memberRepository.findMemberByEmail(email)
			.orElseThrow(() -> new RestApiException(MemberErrorCode.NOT_FOUND_MEMBER));
		// thenR
		Assertions.assertThat(findMember).isNotNull();
	}
}
