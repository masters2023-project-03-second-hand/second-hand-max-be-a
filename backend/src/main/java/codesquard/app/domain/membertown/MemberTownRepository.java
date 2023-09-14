package codesquard.app.domain.membertown;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTownRepository extends JpaRepository<MemberTown, Long> {
	int countMemberTownById(Long id);

	List<MemberTown> findAllByMemberId(Long memberId);

	Optional<MemberTown> findMemberTownByMemberIdAndName(Long memberId, String name);

	void deleteMemberTownByMemberIdAndRegionId(Long memberId, Long regionId);
}
