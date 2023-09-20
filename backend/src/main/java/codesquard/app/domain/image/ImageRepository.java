package codesquard.app.domain.image;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImageRepository extends JpaRepository<Image, Long> {
	List<Image> findAllByItemId(Long itemId);

	@Modifying
	@Query("delete from Image image where image.item.id = :itemId and image.imageUrl in :imageUrls")
	int deleteImagesByItemIdAndImageUrlIn(
		@Param("itemId") Long itemId,
		@Param("imageUrls") List<String> imageUrls);

	int countImageByItemId(Long itemId);

	@Modifying
	@Query("update Image set thumbnail = false where item.id = :itemId and thumbnail = true")
	int updateThumnailToFalseByItemIdAndThumbnailIsTrue(@Param("itemId") Long itemId);

	@Modifying
	@Query("update Image set thumbnail = :thumbnail where item.id = :itemId and imageUrl = :imageUrl")
	int updateThumbnailByItemIdAndImageUrl(
		@Param("itemId") Long itemId,
		@Param("imageUrl") String imageUrl,
		@Param("thumbnail") boolean thumbnail);

	int deleteByItemId(Long itemId);
}
