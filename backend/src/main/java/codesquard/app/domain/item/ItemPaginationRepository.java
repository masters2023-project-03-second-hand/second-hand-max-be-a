package codesquard.app.domain.item;

import static codesquard.app.domain.item.QItem.*;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import codesquard.app.api.response.ItemResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class ItemPaginationRepository {

	private final JPAQueryFactory queryFactory;

	public Slice<ItemResponse> findByIdAndRegion(Long itemId, String region, int size, Long categoryId) {
		List<ItemResponse> itemResponses = queryFactory.select(Projections.fields(ItemResponse.class,
				item.id.as("itemId"),
				item.thumbnailUrl,
				item.title,
				item.region,
				item.createdAt,
				item.price,
				item.status,
				item.wishCount,
				item.chatCount))
			.from(item)
			.where(lessThanItemId(itemId),
				equalCategoryId(categoryId),
				equalTradingRegion(region)
			)
			.orderBy(item.createdAt.desc())
			.limit(size + 1)
			.fetch();
		return checkLastPage(size, itemResponses);
	}

	private BooleanExpression lessThanItemId(Long itemId) {
		if (itemId == null) {
			return null;
		}

		return item.id.lt(itemId);
	}

	private BooleanExpression equalCategoryId(Long categoryId) {
		if (categoryId == null) {
			return null;
		}

		return item.category.id.eq(categoryId);
	}

	private BooleanExpression equalTradingRegion(String region) {
		if (region == null) {
			return null;
		}

		return item.region.like(region + "%");
	}

	private Slice<ItemResponse> checkLastPage(int size, List<ItemResponse> results) {

		boolean hasNext = false;

		// 조회한 결과 개수가 요청한 페이지 사이즈보다 다음 페이지 존재, next = true
		if (results.size() > size) {
			hasNext = true;
			results.remove(size);
		}

		return new SliceImpl<>(results, PageRequest.ofSize(size), hasNext);
	}
}
