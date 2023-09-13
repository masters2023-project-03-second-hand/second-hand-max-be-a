package codesquard.app.domain.pagination;

import static codesquard.app.domain.item.QItem.*;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.querydsl.core.types.dsl.BooleanExpression;

import codesquard.app.api.response.ItemResponse;
import codesquard.app.api.response.ItemResponses;
import codesquard.app.domain.item.ItemStatus;
import codesquard.app.domain.sales.SalesStatus;

public class PaginationUtils {

	public static BooleanExpression lessThanItemId(Long itemId) {
		if (itemId == null) {
			return null;
		}

		return item.id.lt(itemId);
	}

	public static BooleanExpression equalCategoryId(Long categoryId) {
		if (categoryId == null) {
			return null;
		}

		return item.category.id.eq(categoryId);
	}

	public static BooleanExpression equalTradingRegion(String region) {
		if (region == null) {
			return null;
		}

		return item.region.like(region + "%");
	}

	public static BooleanExpression equalsStatus(SalesStatus status) {
		if (status == SalesStatus.ON_SALE) {
			return item.status.in(ItemStatus.ON_SALE, ItemStatus.RESERVED);
		} else if (status == SalesStatus.SOLD_OUT) {
			return item.status.eq(ItemStatus.SOLD_OUT);
		}
		return item.status.in(ItemStatus.ON_SALE, ItemStatus.RESERVED, ItemStatus.SOLD_OUT);
	}

	public static Slice<ItemResponse> checkLastPage(int size, List<ItemResponse> results) {

		boolean hasNext = false;

		// 조회한 결과 개수가 요청한 페이지 사이즈보다 다음 페이지 존재, next = true
		if (results.size() > size) {
			hasNext = true;
			results.remove(size);
		}

		return new SliceImpl<>(results, PageRequest.ofSize(size), hasNext);
	}

	public static ItemResponses getItemResponses(Slice<ItemResponse> itemResponses) {
		List<ItemResponse> contents = itemResponses.getContent();

		boolean hasNext = itemResponses.hasNext();
		Long nextCursor = null;
		if (hasNext) {
			nextCursor = contents.get(contents.size() - 1).getItemId();
		}
		return new ItemResponses(contents, hasNext, nextCursor);
	}
}
