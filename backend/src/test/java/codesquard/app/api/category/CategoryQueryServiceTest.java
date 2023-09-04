package codesquard.app.api.category;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import codesquard.app.IntegrationTestSupport;
import codesquard.app.api.category.response.CategoryListResponse;

class CategoryQueryServiceTest extends IntegrationTestSupport {

	@BeforeEach
	void cleanup() {
		categoryRepository.deleteAllInBatch();
	}

	@DisplayName("모든 카테고리 목록을 조회한다")
	@Test
	public void findAll() {
		// given
		categoryRepository.saveAll(CategoryFixedFactory.createFixedCategories());
		// when
		CategoryListResponse response = categoryQueryService.findAll();
		// then
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(response.getCategories()).hasSize(2);
			softAssertions.assertAll();
		});
	}
}
