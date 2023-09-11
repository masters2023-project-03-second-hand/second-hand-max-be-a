package codesquard.app.domain.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findMemberByLoginId(String loginId);

	Optional<Member> findMemberByLoginIdAndEmail(String loginId, String email);

	boolean existsMemberByLoginId(String loginId);

	Optional<Member> findMemberByEmail(String email);

	boolean existsMemberByEmail(String email);
}
