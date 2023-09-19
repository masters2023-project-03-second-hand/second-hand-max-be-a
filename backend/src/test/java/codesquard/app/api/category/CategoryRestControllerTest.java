package codesquard.app.api.category;

import static codesquard.app.api.category.CategoryTestSupport.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import codesquard.app.ControllerTestSupport;
import codesquard.app.api.category.response.CategoryListResponse;
import codesquard.app.api.errors.handler.GlobalExceptionHandler;
import codesquard.app.domain.oauth.support.AuthPrincipalArgumentResolver;

class CategoryRestControllerTest extends ControllerTestSupport {

	private MockMvc mockMvc;

	@MockBean
	private AuthPrincipalArgumentResolver authPrincipalArgumentResolver;

	@Autowired
	private CategoryRestController categoryRestController;

	@Autowired
	private GlobalExceptionHandler globalExceptionHandler;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(categoryRestController)
			.setControllerAdvice(globalExceptionHandler)
			.setCustomArgumentResolvers(authPrincipalArgumentResolver)
			.alwaysDo(print())
			.build();
	}

	@DisplayName("카테고리 목록을 조회한다")
	@Test
	public void findAll() throws Exception {
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
