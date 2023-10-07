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

	@Query("select memberTown.region.id from MemberTown memberTown where memberTown.member.id = :memberId and memberTown.isSelected = :isSelected")
	Long findRegionIdByMemberIdAndIsSelected(
		@Param("memberId") Long memberId,
		@Param("isSelected") boolean isSelected);

	@Modifying
	@Query("update MemberTown memberTown set memberTown.isSelected = :isSelected where memberTown.region.id = :regionId and memberTown.member.id = :memberId")
	int changeIsSelect(
		@Param("isSelected") boolean isSelected,
		@Param("regionId") Long regionId,
		@Param("memberId") Long memberId);

	void deleteMemberTownByMemberIdAndRegionId(Long memberId, Long regionId);
}
