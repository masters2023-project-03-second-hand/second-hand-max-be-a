package codesquard.app.api.category;

import static codesquard.app.CategoryTestSupport.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import codesquard.app.ControllerTestSupport;
import codesquard.app.api.category.response.CategoryListResponse;

@ActiveProfiles("test")
@WebMvcTest(controllers = CategoryRestController.class)
class CategoryRestControllerTest extends ControllerTestSupport {

	private MockMvc mockMvc;

	@MockBean
	private CategoryQueryService categoryQueryService;

	@Autowired
	private CategoryRestController categoryRestController;

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(categoryRestController)
			.setControllerAdvice(globalExceptionHandler)
			.setCustomArgumentResolvers(authPrincipalArgumentResolver)
			.alwaysDo(print())
			.build();
	}

	@DisplayName("카테고리 목록을 조회한다")
	@Test
	void findAll() throws Exception {
		// given
		CategoryListResponse response = new CategoryListResponse(getCategories());
		given(categoryQueryService.findAll()).willReturn(response);

		// when & then
		mockMvc.perform(get("/api/categories"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("statusCode").value(Matchers.equalTo(200)))
			.andExpect(jsonPath("message").value(Matchers.equalTo("카테고리 조회에 성공하였습니다.")))
			.andExpect(jsonPath("data.categories").isArray());
	}
}
