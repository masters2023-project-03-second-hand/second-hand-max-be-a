package codesquard.app.api.wishitem;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import codesquard.app.api.response.ApiResponse;
import codesquard.app.api.response.ItemResponses;
import codesquard.app.domain.oauth.support.AuthPrincipal;
import codesquard.app.domain.oauth.support.Principal;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wishes")
@RequiredArgsConstructor
public class WishItemController {

	private final WishItemService wishItemService;

	@PostMapping
	public ApiResponse<Void> wishStatus(@RequestParam(required = false) Long itemId,
		@RequestParam(required = false) String status, @AuthPrincipal Principal principal) {
		wishItemService.register(itemId, principal.getMemberId(), status);
		return ApiResponse.ok("관심상품 변경이 완료되었습니다.", null);
	}

	@GetMapping
	public ApiResponse<ItemResponses> findAll(@RequestParam(required = false) Long categoryId,
		@RequestParam(required = false, defaultValue = "10") int size, @RequestParam(required = false) Long cursor) {
		return ApiResponse.ok("관심상품 조회에 성공하였습니다.", wishItemService.findAll(categoryId, size, cursor));
	}
}
