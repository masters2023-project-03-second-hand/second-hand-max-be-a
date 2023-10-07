package codesquard.app.domain.wish;

import static codesquard.app.domain.item.QItem.*;
import static codesquard.app.domain.wish.QWish.*;

import java.util.List;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import codesquard.app.api.item.response.ItemResponse;
import codesquard.app.domain.item.ItemPaginationRepository;
import codesquard.app.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class WishPaginationRepository {

	private final JPAQueryFactory queryFactory;
	private final ItemRepository itemRepository;
	private final WishRepository wishRepository;
	private final ItemPaginationRepository itemPaginationRepository;

	public Slice<ItemResponse> findAll(Long categoryId, int size, Long cursor, Long memberId) {
		List<ItemResponse> itemResponses = queryFactory.select(Projections.fields(ItemResponse.class,
				item.id.as("itemId"),
				item.thumbnailUrl,
				item.title,
				item.region.as("tradingRegion"),
				item.createdAt,
				item.price,
				item.status,
				item.wishCount,
				item.chatCount,
				item.member.loginId.as("sellerId")))
			.from(wish)
			.join(wish.item, item)
			.on(wish.item.id.eq(item.id))
			.where(itemPaginationRepository.lessThanItemId(cursor),
				itemPaginationRepository.equalCategoryId(categoryId),
				equalMemberId(memberId))
			.orderBy(wish.createdAt.desc())
			.limit(size + 1)
			.fetch();
		return itemRepository.checkLastPage(size, itemResponses);
	}

	public BooleanExpression equalMemberId(Long memberId) {
		if (memberId == null) {
			return null;
		}
		return QWish.wish.member.id.eq(memberId);
	}
}
