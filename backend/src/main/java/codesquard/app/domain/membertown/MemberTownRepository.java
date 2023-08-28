package codesquard.app.domain.membertown;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberTownRepository extends JpaRepository<MemberTown, Long> {
}
