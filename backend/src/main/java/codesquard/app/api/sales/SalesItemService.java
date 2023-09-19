package codesquard.app.api.sales;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.api.item.response.ItemResponse;
import codesquard.app.api.item.response.ItemResponses;
import codesquard.app.domain.pagination.PaginationUtils;
import codesquard.app.domain.sales.SalesPaginationRepository;
import codesquard.app.domain.sales.SalesStatus;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalesItemService {

	private final SalesPaginationRepository salesPaginationRepository;

	@Transactional(readOnly = true)
	public ItemResponses findAll(SalesStatus status, int size, Long cursor) {
		Slice<ItemResponse> itemResponses = salesPaginationRepository.findAll(status, size, cursor);
		return PaginationUtils.getItemResponses(itemResponses);
	}
}
