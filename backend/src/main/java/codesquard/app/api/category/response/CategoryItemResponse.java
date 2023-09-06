package codesquard.app.api.category.response;

import codesquard.app.domain.category.Category;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryItemResponse {

	private Long id;
	private String imageUrl;
	private String name;

	private CategoryItemResponse(Long id, String imageUrl, String name) {
		this.id = id;
		this.imageUrl = imageUrl;
		this.name = name;
	}

	public static CategoryItemResponse from(Category category) {
		return new CategoryItemResponse(category.getId(), category.getImageUrl(), category.getName());
	}
}
