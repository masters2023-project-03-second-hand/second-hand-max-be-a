package codesquard.app.domain.membertown;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTownRepository extends JpaRepository<MemberTown, Long> {
	int countMemberTownById(Long id);
}
