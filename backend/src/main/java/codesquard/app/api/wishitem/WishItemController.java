package codesquard.app.api.wishitem;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import codesquard.app.api.item.response.ItemResponses;
import codesquard.app.api.response.ApiResponse;
import codesquard.app.api.wishitem.response.WishCategoryListResponse;
import codesquard.app.domain.oauth.support.AuthPrincipal;
import codesquard.app.domain.oauth.support.Principal;
import codesquard.app.domain.wish.WishStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/wishes")
@RequiredArgsConstructor
public class WishItemController {

	private final WishItemService wishItemService;

	@PostMapping("/{itemId}")
	public ApiResponse<Void> wishStatus(@PathVariable Long itemId, @RequestParam WishStatus wish,
		@AuthPrincipal Principal principal) {
		wishItemService.changeWishStatus(itemId, principal.getMemberId(), wish);
		return ApiResponse.ok("관심상품 변경이 완료되었습니다.", null);
	}

	@GetMapping
	public ApiResponse<ItemResponses> findAll(@RequestParam(required = false) Long categoryId,
		@RequestParam(required = false, defaultValue = "10") int size, @RequestParam(required = false) Long cursor,
		@AuthPrincipal Principal principal) {
		return ApiResponse.ok("관심상품 조회에 성공하였습니다.", wishItemService.findAll(categoryId, size, cursor, principal));
	}

	@GetMapping("/categories")
	public ApiResponse<WishCategoryListResponse> readWishCategories(@AuthPrincipal Principal principal) {
		log.info("관심 상품들의 카테고리 목록 요청 : {}", principal);
		return ApiResponse.ok("관심상품의 카테고리 목록 조회를 완료하였습니다.", wishItemService.readWishCategories(principal));
	}
}
