package codesquard.app.domain.pagination;

import static codesquard.app.domain.item.QItem.*;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.querydsl.core.types.dsl.BooleanExpression;

import codesquard.app.api.response.ItemResponse;

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

	public static Slice<ItemResponse> checkLastPage(int size, List<ItemResponse> results) {

		boolean hasNext = false;

		// 조회한 결과 개수가 요청한 페이지 사이즈보다 다음 페이지 존재, next = true
		if (results.size() > size) {
			hasNext = true;
			results.remove(size);
		}

		return new SliceImpl<>(results, PageRequest.ofSize(size), hasNext);
	}
}
