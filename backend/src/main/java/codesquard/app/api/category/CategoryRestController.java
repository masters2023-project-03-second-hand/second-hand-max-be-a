package codesquard.app.api.category;

import javax.validation.Valid;

import org.apache.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import codesquard.app.api.category.request.CategorySelectedRequest;
import codesquard.app.api.category.response.CategoryListResponse;
import codesquard.app.api.response.ApiResponse;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api/categories")
@RequiredArgsConstructor
@RestController
public class CategoryRestController {

	private final CategoryQueryService categoryQueryService;

	@ResponseStatus(HttpStatus.OK)
	@GetMapping
	public ApiResponse<CategoryListResponse> findAll() {
		return ApiResponse.of(HttpStatus.OK, "카테고리 조회에 성공하였습니다.", categoryQueryService.findAll());
	}

	@PostMapping
	public ResponseEntity<Void> selectCategory(@Valid @RequestBody CategorySelectedRequest request) {
		categoryQueryService.validateCategoryId(request);
		String categoryId = String.valueOf(request.getSelectedCategoryId());
		return ResponseEntity.status(HttpStatus.FOUND)
			.header(HttpHeaders.LOCATION, "/api/items?categoryId=" + categoryId)
			.body(null);
	}
}
