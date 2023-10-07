package codesquard.app.api.item;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import codesquard.app.api.item.request.ItemModifyRequest;
import codesquard.app.api.item.request.ItemRegisterRequest;
import codesquard.app.api.item.request.ItemStatusModifyRequest;
import codesquard.app.api.item.response.ItemDetailResponse;
import codesquard.app.api.item.response.ItemResponses;
import codesquard.app.api.response.ApiResponse;
import codesquard.app.api.success.successcode.ItemSuccessCode;
import codesquard.app.domain.oauth.support.AuthPrincipal;
import codesquard.app.domain.oauth.support.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

	private final ItemService itemService;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<Void> register(@RequestPart("item") ItemRegisterRequest request,
		@RequestPart(value = "images", required = false) List<MultipartFile> itemImage,
		@RequestPart("thumbnailImage") MultipartFile thumbnail,
		@AuthPrincipal Principal principal) {
		itemService.register(request, itemImage, thumbnail, principal.getMemberId());
		return ApiResponse.success(ItemSuccessCode.CREATED_ITEM);
	}

	@GetMapping
	public ApiResponse<ItemResponses> findAll(
		@RequestParam String region,
		@RequestParam(required = false, defaultValue = "10") int size,
		@RequestParam(required = false) Long cursor,
		@RequestParam(required = false) Long categoryId) {
		return ApiResponse.success(ItemSuccessCode.OK_ITEMS, itemService.findAll(region, size, cursor, categoryId));
	}

	@GetMapping("/{itemId}")
	public ApiResponse<ItemDetailResponse> findDetailItem(@PathVariable Long itemId,
		@AuthPrincipal Principal principal) {
		ItemDetailResponse response = itemService.findDetailItemBy(itemId, principal);
		log.debug("상품 상세 조회 결과 : {}", response);
		return ApiResponse.success(ItemSuccessCode.OK_DETAILED_ITEM, response);
	}

	@PatchMapping("/{itemId}")
	public ApiResponse<Void> modifyItem(@PathVariable Long itemId,
		@RequestPart(value = "images", required = false) List<MultipartFile> addImages,
		@Valid @RequestPart("item") ItemModifyRequest request,
		@RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage,
		@AuthPrincipal Principal principal) {
		itemService.modifyItem(itemId, request, addImages, thumbnailImage, principal);
		return ApiResponse.success(ItemSuccessCode.OK_MODIFIED_ITEM);
	}

	@PutMapping("/{itemId}/status")
	public ApiResponse<Void> modifyItemStatus(@PathVariable Long itemId, @RequestBody ItemStatusModifyRequest request,
		@AuthPrincipal Principal principal) {
		itemService.changeItemStatus(itemId, request.getStatus(), principal);
		return ApiResponse.success(ItemSuccessCode.OK_MODIFIED_STATUS_ITEM);
	}

	@DeleteMapping("/{itemId}")
	public ApiResponse<Void> deleteItem(@PathVariable Long itemId, @AuthPrincipal Principal principal) {
		itemService.deleteItem(itemId, principal);
		return ApiResponse.success(ItemSuccessCode.OK_DELETED_ITEM);
	}
}
