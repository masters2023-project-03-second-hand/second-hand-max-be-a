package codesquard.app.domain.item;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import codesquard.app.api.item.response.ItemResponse;

public interface ItemRepository extends JpaRepository<Item, Long> {

	default SliceImpl<ItemResponse> checkLastPage(int size, List<ItemResponse> results) {

		boolean hasNext = false;

		// 조회한 결과 개수가 요청한 페이지 사이즈보다 다음 페이지 존재, next = true
		if (results.size() > size) {
			hasNext = true;
			results.remove(size);
		}

		return new SliceImpl<>(results, PageRequest.ofSize(size), hasNext);
	}

	@Modifying
	@Query("update Item item set item.viewCount =:viewCount where item.id =:itemId")
	void addViewCountFromRedis(@Param("itemId") Long itemId, @Param("viewCount") Long viewCount);

	@Query("select item.viewCount from Item item where item.id =:itemId")
	Long findViewCountById(@Param("itemId") Long itemId);

	Optional<Item> findItemByIdAndMemberId(Long itemId, Long memberId);

	List<Item> findAllByMemberId(Long memberId);
}
