package codesquard.app.api.sales;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import codesquard.app.api.response.ApiResponse;
import codesquard.app.api.response.ItemResponses;
import codesquard.app.domain.sales.SalesStatus;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sales/history")
@RequiredArgsConstructor
public class SalesItemController {

	private final SalesItemService salesItemService;

	@GetMapping
	public ApiResponse<ItemResponses> findAll(@RequestParam(required = false, defaultValue = "all") SalesStatus status,
		@RequestParam(required = false, defaultValue = "10") int size, @RequestParam(required = false) Long cursor) {
		return ApiResponse.ok("판매 내역 조회에 성공하였습니다.", salesItemService.findAll(status, size, cursor));
	}
}
