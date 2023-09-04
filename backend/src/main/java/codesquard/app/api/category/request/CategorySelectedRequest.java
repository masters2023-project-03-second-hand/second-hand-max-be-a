package codesquard.app.api.category.request;

import javax.validation.constraints.Positive;

import lombok.Getter;

@Getter
public class CategorySelectedRequest {
	@Positive(message = "카테고리 아이디는 양수어야 합니다.")
	private Long selectedCategoryId;

	private CategorySelectedRequest() {

	}
}
