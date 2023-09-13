package codesquard.app.domain.sales;

import static codesquard.app.domain.item.QItem.*;

import java.util.List;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import codesquard.app.api.response.ItemResponse;
import codesquard.app.domain.pagination.PaginationUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class SalesPaginationRepository extends PaginationUtils {

	private final JPAQueryFactory queryFactory;

	public Slice<ItemResponse> findAll(SalesStatus status, int size, Long cursor) {
		List<ItemResponse> itemResponses = queryFactory.select(Projections.fields(ItemResponse.class,
				item.id.as("itemId"),
				item.thumbnailUrl,
				item.title,
				item.region,
				item.createdAt,
				item.price,
				item.status))
			.from(item)
			.where(lessThanItemId(cursor),
				equalsStatus(status))
			.orderBy(item.createdAt.desc())
			.limit(size + 1)
			.fetch();
		return checkLastPage(size, itemResponses);
	}
}