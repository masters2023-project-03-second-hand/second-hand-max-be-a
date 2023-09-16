package codesquard.app.api.category;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.stream.Stream;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import codesquard.app.ControllerTestSupport;
import codesquard.app.api.category.request.CategorySelectedRequest;
import codesquard.app.api.category.response.CategoryListResponse;
import codesquard.app.api.errors.errorcode.CategoryErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.errors.handler.GlobalExceptionHandler;
import codesquard.app.domain.category.Category;
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
		CategoryListResponse response = new CategoryListResponse(List.of(Category.sport()));
		given(categoryQueryService.findAll()).willReturn(response);
		// when & then
		mockMvc.perform(get("/api/categories"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("statusCode").value(Matchers.equalTo(200)))
			.andExpect(jsonPath("message").value(Matchers.equalTo("카테고리 조회에 성공하였습니다.")))
			.andExpect(jsonPath("data.categories").isArray());
	}

	@DisplayName("카테고리 아이디는 양수여야 한다")
	@MethodSource(value = "invalidCategoryId")
	@ParameterizedTest
	public void selectCategoryWithZeroCategoryId(Long categoryId) throws Exception {
		// given
		CategorySelectedRequest request = CategoryFixedFactory.createFixedCategorySelectedRequest(categoryId);
		willThrow(new RestApiException(CategoryErrorCode.NOT_FOUND_CATEGORY))
			.given(categoryQueryService)
			.validateCategoryId(any());
		// when & then
		mockMvc.perform(post("/api/categories")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("statusCode").value(Matchers.equalTo(400)))
			.andExpect(jsonPath("message").value(Matchers.equalTo("유효하지 않은 입력형식입니다.")))
			.andExpect(jsonPath("data[0].field").value(Matchers.equalTo("selectedCategoryId")))
			.andExpect(jsonPath("data[0].defaultMessage").value(Matchers.equalTo("카테고리 아이디는 양수여야 합니다.")));
	}

	private static Stream<Arguments> invalidCategoryId() {
		return Stream.of(
			Arguments.of(0L),
			Arguments.of(-1L)
		);
	}
}
