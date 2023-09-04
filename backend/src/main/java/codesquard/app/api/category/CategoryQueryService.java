package codesquard.app.api.category;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.api.category.response.CategoryItemResponse;
import codesquard.app.api.category.response.CategoryListResponse;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.category.CategoryRepository;
import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CategoryQueryService {
	private final CategoryRepository categoryRepository;

	public CategoryListResponse findAll() {
		List<Category> categories = categoryRepository.findAll();
		List<CategoryItemResponse> categoryItemResponses = categories.stream()
			.map(CategoryItemResponse::from)
			.collect(Collectors.toUnmodifiableList());
		return CategoryListResponse.create(categoryItemResponses);
	}
}
