package codesquard.app.api.category;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
}
