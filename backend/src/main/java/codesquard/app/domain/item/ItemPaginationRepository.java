package codesquard.app.domain.item;

import static codesquard.app.domain.item.QItem.*;

import java.util.List;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import codesquard.app.api.item.response.ItemResponse;
import codesquard.app.domain.sales.SalesStatus;
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
				item.chatCount,
				item.member.loginId.as("sellerId")))
			.from(item)
			.where(lessThanItemId(itemId),
				equalCategoryId(categoryId),
				equalTradingRegion(region)
			)
			.orderBy(item.createdAt.desc())
			.limit(size + 1)
			.fetch();
		return itemRepository.checkLastPage(size, itemResponses);
	}

	public BooleanExpression lessThanItemId(Long itemId) {
		if (itemId == null) {
			return null;
		}

		return item.id.lt(itemId);
	}

	public BooleanExpression equalCategoryId(Long categoryId) {
		if (categoryId == null) {
			return null;
		}
		return item.category.id.eq(categoryId);
	}

	public BooleanExpression equalMemberId(Long memberId) {
		if (memberId == null) {
			return item.member.id.eq(-1L);
		}
		return item.member.id.eq(memberId);
	}

	public BooleanExpression equalTradingRegion(String region) {
		if (region == null) {
			return null;
		}

		return item.region.like(region + "%");
	}

	public BooleanExpression equalsStatus(SalesStatus status) {
		if (status == SalesStatus.ON_SALE) {
			return item.status.in(ItemStatus.ON_SALE, ItemStatus.RESERVED);
		} else if (status == SalesStatus.SOLD_OUT) {
			return item.status.eq(ItemStatus.SOLD_OUT);
		}
		return item.status.in(ItemStatus.ON_SALE, ItemStatus.RESERVED, ItemStatus.SOLD_OUT);
	}

}
