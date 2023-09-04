package codesquard.app.api.category.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class CategoryListResponse {
	List<CategoryItemResponse> categories;

	private CategoryListResponse() {

	}

	public CategoryListResponse(List<CategoryItemResponse> categories) {
		this.categories = new ArrayList<>(categories);
	}

	public static CategoryListResponse create(List<CategoryItemResponse> categoryItemResponses) {
		return new CategoryListResponse(categoryItemResponses);
	}
}
