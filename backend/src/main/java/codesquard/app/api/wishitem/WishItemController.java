package codesquard.app.api.wishitem;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import codesquard.app.api.errors.errorcode.WishErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.response.ApiResponse;
import codesquard.app.domain.oauth.support.AuthPrincipal;
import codesquard.app.domain.oauth.support.Principal;
import codesquard.app.domain.wish.Wish;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wishes")
@RequiredArgsConstructor
public class WishItemController {

	private final WishItemService wishItemService;

	@PostMapping
	public ApiResponse<Void> wishStatus(@RequestParam(required = false) Long itemId,
		@RequestParam(required = false) String status, @AuthPrincipal Principal principal) {
		if (status.equals("yes")) {
			wishItemService.register(itemId, principal.getMemberId());
		} else if (status.equals("no")) {
			wishItemService.cancel(itemId);
		} else {
			throw new RestApiException(WishErrorCode.INVALID_PARAMETER);
		}
		return ApiResponse.ok("관심상품 변경이 완료되었습니다.", null);
	}

	@GetMapping
	public ApiResponse<List<Wish>> findAll(@RequestParam(required = false) Long categoryId,
		@RequestParam(required = false, defaultValue = "10") int size, @RequestParam(required = false) Long cursor) {
		return ApiResponse.ok("관심상품 조회에 성공하였습니다.", wishItemService.findAll(categoryId, size, cursor));
	}
}
