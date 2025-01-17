package codesquard.app.domain.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findMemberByLoginId(String loginId);

	Optional<Member> findMemberByLoginIdAndEmail(String loginId, String email);

	Optional<Member> findMemberByEmail(String email);

	boolean existsMemberByLoginId(String loginId);

	boolean existsMemberByEmail(String email);
}
