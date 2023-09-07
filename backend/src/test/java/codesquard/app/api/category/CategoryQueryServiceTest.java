package codesquard.app.api.category;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import codesquard.app.IntegrationTestSupport;
import codesquard.app.api.category.request.CategorySelectedRequest;
import codesquard.app.api.category.response.CategoryListResponse;
import codesquard.app.api.errors.exception.RestApiException;

class CategoryQueryServiceTest extends IntegrationTestSupport {

	@DisplayName("모든 카테고리 목록을 조회한다")
	@Test
	public void findAll() {
		// given
		categoryRepository.saveAll(CategoryFixedFactory.createFixedCategories());
		// when
		CategoryListResponse response = categoryQueryService.findAll();
		// then
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(response.getCategories()).hasSize(3);
			softAssertions.assertAll();
		});
	}

	@DisplayName("카테고리 아이디가 존재하지 않아 예외가 발생한다")
	@Test
	public void validateCategoryId() {
		// given
		categoryRepository.saveAll(CategoryFixedFactory.createFixedCategories());
		CategorySelectedRequest request = CategoryFixedFactory.createFixedCategorySelectedRequest(9999L);
		// when
		Throwable throwable = Assertions.catchThrowable(() -> categoryQueryService.validateCategoryId(request));
		// then
		Assertions.assertThat(throwable)
			.isInstanceOf(RestApiException.class)
			.extracting("errorCode.message")
			.isEqualTo("카테고리를 찾을 수 없습니다.");
	}
}
