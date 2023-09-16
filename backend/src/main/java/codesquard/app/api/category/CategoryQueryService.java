package codesquard.app.api.category;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.api.category.request.CategorySelectedRequest;
import codesquard.app.api.category.response.CategoryListResponse;
import codesquard.app.api.errors.errorcode.CategoryErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
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
		return new CategoryListResponse(categories);
	}

	public void validateCategoryId(CategorySelectedRequest request) {
		if (!categoryRepository.existsById(request.getSelectedCategoryId())) {
			throw new RestApiException(CategoryErrorCode.NOT_FOUND_CATEGORY);
		}
	}
}
