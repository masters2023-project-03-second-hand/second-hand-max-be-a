package codesquard.app.domain.sales;

import static codesquard.app.domain.item.QItem.*;

import java.util.List;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import codesquard.app.api.item.response.ItemResponse;
import codesquard.app.domain.item.ItemPaginationRepository;
import codesquard.app.domain.item.ItemRepository;
import codesquard.app.domain.oauth.support.Principal;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class SalesPaginationRepository {

	private final JPAQueryFactory queryFactory;
	private final ItemRepository itemRepository;
	private final ItemPaginationRepository itemPaginationRepository;

	public Slice<ItemResponse> findAll(SalesStatus status, int size, Long cursor, Principal principal) {
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
			.where(
				itemPaginationRepository.equalMemberId(principal.getMemberId()),
				itemPaginationRepository.lessThanItemId(cursor),
				itemPaginationRepository.equalsStatus(status))
			.orderBy(item.createdAt.desc())
			.limit(size + 1)
			.fetch();
		return itemRepository.checkLastPage(size, itemResponses);
	}
}
