package codesquard.app.api.category;

import static codesquard.app.CategoryTestSupport.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import codesquard.app.api.category.response.CategoryListResponse;
import codesquard.app.domain.category.CategoryRepository;

@ActiveProfiles("test")
@SpringBootTest
class CategoryQueryServiceTest {

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	CategoryQueryService categoryQueryService;

	@AfterEach
	void tearDown() {
		categoryRepository.deleteAllInBatch();
	}

	@DisplayName("모든 카테고리 목록을 조회한다")
	@Test
	void findAll() {
		// given
		categoryRepository.saveAll(getCategories());

		// when
		CategoryListResponse response = categoryQueryService.findAll();

		// then
		assertThat(response.getCategories()).hasSize(23);
	}
}
