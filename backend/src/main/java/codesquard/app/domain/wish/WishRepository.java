package codesquard.app.domain.wish;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {

	void deleteByItemId(Long itemId);

	int countWishByItemId(Long itemId);

	boolean existsByMemberIdAndItemId(Long memberId, Long itemId);

	List<Wish> findAllByMemberId(Long memberId);
}
