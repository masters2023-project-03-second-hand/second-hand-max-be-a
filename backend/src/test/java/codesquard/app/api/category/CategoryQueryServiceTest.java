package codesquard.app.api.category;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import codesquard.app.CategoryTestSupport;
import codesquard.app.IntegrationTestSupport;
import codesquard.app.api.category.response.CategoryListResponse;
import codesquard.app.domain.category.Category;

class CategoryQueryServiceTest extends IntegrationTestSupport {

	@DisplayName("모든 카테고리 목록을 조회한다")
	@Test
	public void findAll() {
		// given
		categoryRepository.deleteAllInBatch();

		List<Category> categories = CategoryTestSupport.getCategories();
		categoryRepository.saveAll(categories);
		// when
		CategoryListResponse response = categoryQueryService.findAll();
		// then
		assertThat(response.getCategories()).hasSize(23);
	}
}
