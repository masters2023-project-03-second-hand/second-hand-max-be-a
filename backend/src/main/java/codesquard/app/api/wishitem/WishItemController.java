package codesquard.app.api.wishitem;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import codesquard.app.api.response.ApiResponse;
import codesquard.app.domain.oauth.support.AuthPrincipal;
import codesquard.app.domain.oauth.support.Principal;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wishes")
@RequiredArgsConstructor
public class WishItemController {

	private final WishItemService wishItemService;

	@PostMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<Void> wishStatus(@RequestParam(required = false) Long itemId,
		@RequestParam(required = false) String status, @AuthPrincipal Principal principal) {
		if (status.equals("yes")) {
			wishItemService.register(itemId, principal.getMemberId());
		} else {
			wishItemService.cancel(itemId);
		}
		return ApiResponse.ok("관심상품 변경이 완료되었습니다.", null);
	}
}
