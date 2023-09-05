package codesquard.app.api.category.response;

import codesquard.app.domain.category.Category;
import lombok.Getter;

@Getter
public class CategoryItemResponse {
	private Long id;
	private String imageUrl;
	private String name;
	private boolean selected;

	private CategoryItemResponse() {

	}

	public CategoryItemResponse(Long id, String imageUrl, String name, boolean selected) {
		this.id = id;
		this.imageUrl = imageUrl;
		this.name = name;
		this.selected = selected;
	}

	public static CategoryItemResponse from(Category category) {
		return new CategoryItemResponse(category.getId(), category.getImageUrl(), category.getName(), false);
	}
}