package codesquard.app.api.category.response;

import java.util.List;
import java.util.stream.Collectors;

import codesquard.app.domain.category.Category;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryListResponse {

	private List<CategoryItemResponse> categories;

	public CategoryListResponse(List<Category> categories) {
		this.categories = categories.stream()
			.map(CategoryItemResponse::from)
			.collect(Collectors.toUnmodifiableList());
	}
}
