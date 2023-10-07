package codesquard.app.domain.wish;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishRepository extends JpaRepository<Wish, Long> {

	void deleteAllByIdIn(List<Long> wishIds);

	boolean existsByMemberIdAndItemId(Long memberId, Long itemId);

	List<Wish> findAllByMemberId(Long memberId);

	List<Wish> findByItemId(Long itemId);
}
