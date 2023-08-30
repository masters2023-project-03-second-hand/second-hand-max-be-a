package codesquard.app.api.item;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
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

	@PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<ApiResponse<Void>> register(@RequestPart ItemRegisterRequest request,
		@RequestPart List<MultipartFile> itemImage,
		@AuthPrincipal Principal principal) {
		itemService.register(request, itemImage, principal.getMemberId());
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created("상품 등록이 완료되었습니다.", null));
	}
}
