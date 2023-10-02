package codesquard.app.api.category.response;

import java.io.Serializable;

import codesquard.app.domain.category.Category;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryItemResponse implements Serializable {

	private Long id;
	private String imageUrl;
	private String name;

	public static CategoryItemResponse from(Category category) {
		return new CategoryItemResponse(category.getId(), category.getImageUrl(), category.getName());
	}
}
