package codesquard.app.api.category.response;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryListResponse {

	List<CategoryItemResponse> categories;

	public CategoryListResponse(List<CategoryItemResponse> categories) {
		this.categories = new ArrayList<>(categories);
	}

	public static CategoryListResponse create(List<CategoryItemResponse> categoryItemResponses) {
		return new CategoryListResponse(categoryItemResponses);
	}
}
