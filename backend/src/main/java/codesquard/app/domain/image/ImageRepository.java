package codesquard.app.domain.image;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
	List<Image> findAllByItemId(Long itemId);
}
