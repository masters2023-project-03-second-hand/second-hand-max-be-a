package codesquard.app.api.sales;

import static codesquard.app.domain.pagination.PaginationUtils.*;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import codesquard.app.api.response.ItemResponse;
import codesquard.app.api.response.ItemResponses;
import codesquard.app.domain.sales.SalesPaginationRepository;
import codesquard.app.domain.sales.SalesStatus;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalesItemService {

	private final SalesPaginationRepository salesPaginationRepository;

	@Transactional(readOnly = true)
	public ItemResponses findAll(@RequestParam SalesStatus status, @RequestParam(required = false) int size,
		@RequestParam(required = false) Long cursor) {
		Slice<ItemResponse> itemResponses = salesPaginationRepository.findAll(status, size, cursor);
		return getItemResponses(itemResponses);
	}
}
