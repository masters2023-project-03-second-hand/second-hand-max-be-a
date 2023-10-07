package codesquard.app.api.category;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import codesquard.app.api.category.response.CategoryListResponse;
import codesquard.app.api.response.ApiResponse;
import codesquard.app.api.success.successcode.CategorySuccessCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@RestController
public class CategoryRestController {

	private final CategoryQueryService categoryQueryService;

	@ResponseStatus(HttpStatus.OK)
	@GetMapping
	public ApiResponse<CategoryListResponse> findAll() {
		log.info("카테고리 목록 조회 요청");
		return ApiResponse.success(CategorySuccessCode.OK_CATEGORIES, categoryQueryService.findAll());
	}
}
