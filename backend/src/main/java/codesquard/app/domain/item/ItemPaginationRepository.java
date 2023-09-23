package codesquard.app.domain.item;

import static codesquard.app.domain.item.QItem.*;

import java.util.List;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import codesquard.app.api.item.response.ItemResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class ItemPaginationRepository {

	private final JPAQueryFactory queryFactory;
	private final ItemRepository itemRepository;

	public Slice<ItemResponse> findByIdAndRegion(Long itemId, String region, int size, Long categoryId) {
		List<ItemResponse> itemResponses = queryFactory.select(Projections.fields(ItemResponse.class,
				item.id.as("itemId"),
				item.thumbnailUrl,
				item.title,
				item.region.as("tradingRegion"),
				item.createdAt,
				item.price,
				item.status,
				item.wishCount,
				item.chatCount))
			.from(item)
			.where(itemRepository.lessThanItemId(itemId),
				itemRepository.equalCategoryId(categoryId),
				itemRepository.equalTradingRegion(region)
			)
			.orderBy(item.createdAt.desc())
			.limit(size + 1)
			.fetch();
		return itemRepository.checkLastPage(size, itemResponses);
	}
}
