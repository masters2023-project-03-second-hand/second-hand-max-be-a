package codesquard.app.api.category.request;

import javax.validation.constraints.Positive;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategorySelectedRequest {

	@Positive(message = "카테고리 아이디는 양수여야 합니다.")
	private Long selectedCategoryId;

	private CategorySelectedRequest(Long selectedCategoryId) {
		this.selectedCategoryId = selectedCategoryId;
	}

	public static CategorySelectedRequest create(Long categoryId) {
		return new CategorySelectedRequest(categoryId);
	}
}
