package codesquard.app.domain.image;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImageRepository extends JpaRepository<Image, Long> {
	List<Image> findAllByItemId(Long itemId);

	@Modifying
	@Query("delete from Image image where image.imageUrl in :imageUrls")
	int deleteImagesByImageUrlIn(@Param("imageUrls") List<String> imageUrls);

	int countImageByItemId(Long itemId);
}
