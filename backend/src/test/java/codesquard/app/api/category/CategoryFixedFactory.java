package codesquard.app.api.category;

import java.util.List;
import java.util.stream.Collectors;

import codesquard.app.api.category.request.CategorySelectedRequest;
import codesquard.app.api.category.response.CategoryItemResponse;
import codesquard.app.api.category.response.CategoryListResponse;
import codesquard.app.domain.category.Category;

public class CategoryFixedFactory {

	public static List<Category> createFixedCategories() {
		Category category1 = Category.create("디지털기기", "https://i.ibb.co/cxS7Fhc/digital.png");
		Category category2 = Category.create("생활가전", "https://i.ibb.co/F5z7vV9/domestic.png");
		Category category3 = Category.create("가구/인테리어", "https://i.ibb.co/cyYH5V8/furniture.png");
		return List.of(category1, category2, category3);
	}

	public static Category createdFixedCategory() {
		return Category.create("가구/인테리어", "https://i.ibb.co/cyYH5V8/furniture.png");
	}

	public static CategoryListResponse createFixedCategoryListResponse() {
		List<CategoryItemResponse> categoryItemResponses = createFixedCategories().stream()
			.map(CategoryItemResponse::from)
			.collect(Collectors.toUnmodifiableList());
		return CategoryListResponse.create(categoryItemResponses);
	}

	public static CategorySelectedRequest createFixedCategorySelectedRequest(Long categoryId) {
		return CategorySelectedRequest.create(categoryId);
	}
}
