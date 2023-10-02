package codesquard.app.api.wishitem.response;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import codesquard.app.domain.category.Category;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WishCategoryListResponse implements Serializable {
	private List<WishCategoryItemResponse> categories;

	public static WishCategoryListResponse of(List<Category> categories) {
		return new WishCategoryListResponse(categories.stream()
			.map(WishCategoryItemResponse::from)
			.collect(Collectors.toUnmodifiableList())
		);
	}

	@Override
	public String toString() {
		return String.format("%s, %s(categories=%s)", "관심상품 카테고리 리스트 응답", this.getClass().getSimpleName(), categories);
	}
}
