package codesquard.app.api.wishitem.response;

import java.io.Serializable;

import codesquard.app.domain.category.Category;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WishCategoryItemResponse implements Serializable {
	private Long categoryId;
	private String categoryName;

	public static WishCategoryItemResponse from(Category category) {
		return new WishCategoryItemResponse(category.getId(), category.getName());
	}

	@Override
	public String toString() {
		return String.format("%s, %s(categoryId=%d, categoryName=%s)", "관심상품 카테고리 항목", this.getClass().getSimpleName(),
			categoryId, categoryName);
	}
}
