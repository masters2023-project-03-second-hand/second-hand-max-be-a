package codesquard.app.api.item;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import codesquard.app.api.response.ApiResponse;
import codesquard.app.domain.oauth.support.AuthPrincipal;
import codesquard.app.domain.oauth.support.Principal;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

	private final ItemService itemService;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<Void> register(@RequestPart ItemRegisterRequest request,
		@RequestPart List<MultipartFile> itemImage,
		@AuthPrincipal Principal principal) {
		itemService.register(request, itemImage, principal.getMemberId());
		return ApiResponse.created("상품 등록이 완료되었습니다.", null);
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse findAll(@RequestParam String region,
		@RequestParam(required = false, defaultValue = "10") int size, @RequestParam(required = false) Long cursor,
		@RequestParam(required = false) Long categoryId) {
		return ApiResponse.ok("상품 목록 조회에 성공하였습니다.",
			itemService.findAll(region, size, cursor, categoryId));
	}
}
