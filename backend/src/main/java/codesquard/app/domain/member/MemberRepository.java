package codesquard.app.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Member findMemberByLoginId(String loginId);

	Member findMemberByLoginIdAndAndEmail(String loginId, String email);

	boolean existsMemberByLoginId(String loginId);
}
