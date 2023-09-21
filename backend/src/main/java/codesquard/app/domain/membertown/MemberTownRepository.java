package codesquard.app.domain.membertown;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberTownRepository extends JpaRepository<MemberTown, Long> {

	List<MemberTown> findAllByMemberId(Long memberId);

	Optional<MemberTown> findMemberTownByMemberIdAndRegionId(Long memberId, Long regionId);

	Optional<MemberTown> findMemberTownByMemberIdAndName(Long memberId, String name);

	void deleteMemberTownByMemberIdAndRegionId(Long memberId, Long regionId);

	@Modifying
	@Query("update MemberTown memberTown set memberTown.isSelected = false where memberTown.isSelected = true and memberTown.member.id = :memberId")
	int changeIsSelectToFalse(@Param("memberId") Long memberId);

	@Modifying
	@Query("update MemberTown memberTown set memberTown.isSelected = true where memberTown.region.id = :regionId and memberTown.member.id = :memberId")
	int changeIsSelectToTrue(
		@Param("regionId") Long regionId,
		@Param("memberId") Long memberId);
}
