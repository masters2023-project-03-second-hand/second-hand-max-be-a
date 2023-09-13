package codesquard.app.api.category.response;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryListResponse {

	private List<CategoryItemResponse> categories;

	public static CategoryListResponse create(List<CategoryItemResponse> categoryItemResponses) {
		return new CategoryListResponse(categoryItemResponses);
	}
}
