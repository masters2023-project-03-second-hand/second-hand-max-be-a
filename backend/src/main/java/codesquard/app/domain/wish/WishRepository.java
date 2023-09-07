package codesquard.app.domain.wish;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WishRepository extends JpaRepository<Wish, Long> {

	void deleteByItemId(Long itemId);

	@Query("SELECT wish FROM Wish wish WHERE wish.id < :cursor")
	List<Wish> findAll(@Param("cursor") Long cursor, Pageable pageable);

	@Query("SELECT wish FROM Wish wish WHERE wish.id < :cursor AND wish.item.category.id = :categoryId")
	List<Wish> findAllByCategoryId(@Param("categoryId") Long categoryId, @Param("cursor") Long cursor,
		Pageable pageable);
}
