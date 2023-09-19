package codesquard.app.api.category;

import static codesquard.app.CategoryTestSupport.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import codesquard.app.IntegrationTestSupport;
import codesquard.app.api.category.response.CategoryListResponse;

class CategoryQueryServiceTest extends IntegrationTestSupport {

	@DisplayName("모든 카테고리 목록을 조회한다")
	@Test
	public void findAll() {
		// given
		categoryRepository.deleteAllInBatch();
		categoryRepository.saveAll(getCategories());

		// when
		CategoryListResponse response = categoryQueryService.findAll();

		// then
		assertThat(response.getCategories()).hasSize(23);
	}
}
