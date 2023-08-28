package codesquard.app.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
	Member findMemberByLoginIdIs(String loginId);

	boolean existsMemberByLoginIdIs(String loginId);
}
