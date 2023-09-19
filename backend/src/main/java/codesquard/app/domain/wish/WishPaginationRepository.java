package codesquard.app.domain.wish;

import static codesquard.app.domain.item.QItem.*;
import static codesquard.app.domain.wish.QWish.*;

import java.util.List;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import codesquard.app.api.item.response.ItemResponse;
import codesquard.app.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class WishPaginationRepository {

	private final JPAQueryFactory queryFactory;
	private final ItemRepository itemRepository;

	public Slice<ItemResponse> findAll(Long categoryId, int size, Long cursor) {
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
			.from(wish)
			.join(wish.item, item)
			.on(wish.item.id.eq(item.id))
			.where(itemRepository.lessThanItemId(cursor),
				itemRepository.equalCategoryId(categoryId))
			.orderBy(wish.createdAt.desc())
			.limit(size + 1)
			.fetch();
		return itemRepository.checkLastPage(size, itemResponses);
	}
}
