package codesquard.app.domain.item;

import static codesquard.app.domain.item.QItem.*;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.querydsl.core.types.dsl.BooleanExpression;

import codesquard.app.api.item.response.ItemResponse;
import codesquard.app.domain.sales.SalesStatus;

public interface ItemRepository extends JpaRepository<Item, Long> {

	default BooleanExpression lessThanItemId(Long itemId) {
		if (itemId == null) {
			return null;
		}

		return item.id.lt(itemId);
	}

	default BooleanExpression equalCategoryId(Long categoryId) {
		if (categoryId == null) {
			return null;
		}

		return item.category.id.eq(categoryId);
	}

	default BooleanExpression equalTradingRegion(String region) {
		if (region == null) {
			return null;
		}

		return item.region.like(region + "%");
	}

	default BooleanExpression equalsStatus(SalesStatus status) {
		if (status == SalesStatus.ON_SALE) {
			return item.status.in(ItemStatus.ON_SALE, ItemStatus.RESERVED);
		} else if (status == SalesStatus.SOLD_OUT) {
			return item.status.eq(ItemStatus.SOLD_OUT);
		}
		return item.status.in(ItemStatus.ON_SALE, ItemStatus.RESERVED, ItemStatus.SOLD_OUT);
	}

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
}
