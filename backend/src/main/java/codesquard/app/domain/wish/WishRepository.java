package codesquard.app.domain.wish;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {

	void deleteByItemId(Long itemId);

	int countWishByItemId(Long itemId);
}
