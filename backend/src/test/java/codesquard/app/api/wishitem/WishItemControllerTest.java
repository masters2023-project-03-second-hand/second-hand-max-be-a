package codesquard.app.api.wishitem;

import static codesquard.app.CategoryTestSupport.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import codesquard.app.ControllerTestSupport;
import codesquard.app.api.wishitem.response.WishCategoryListResponse;
import codesquard.app.domain.oauth.support.Principal;

@ActiveProfiles("test")
@WebMvcTest(controllers = WishItemController.class)
class WishItemControllerTest extends ControllerTestSupport {
	private MockMvc mockMvc;

	@Autowired
	private MappingJackson2HttpMessageConverter jackson2HttpMessageConverter;

	@Autowired
	private WishItemController wishItemController;

	@MockBean
	private WishItemService wishItemService;

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(wishItemController)
			.setControllerAdvice(globalExceptionHandler)
			.setCustomArgumentResolvers(authPrincipalArgumentResolver)
			.setMessageConverters(jackson2HttpMessageConverter)
			.alwaysDo(print())
			.build();

		given(authPrincipalArgumentResolver.supportsParameter(any())).willReturn(true);

		Principal principal = new Principal(1L, "23Yong@gmail.com", "23Yong", null, null);
		given(authPrincipalArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(principal);
	}

	@DisplayName("관심 상품들의 카테고리 목록을 요청한다")
	@Test
	void readWishCategories() throws Exception {
		// given
		WishCategoryListResponse response = WishCategoryListResponse.of(
			List.of(findByName("스포츠/레저"), findByName("가구/인테리어")));
		given(wishItemService.readWishCategories(ArgumentMatchers.any(Principal.class)))
			.willReturn(response);

		// when & then
		mockMvc.perform(get("/api/wishes/categories"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("statusCode").value(equalTo(200)))
			.andExpect(jsonPath("message").value(equalTo("관심상품의 카테고리 목록 조회를 완료하였습니다.")))
			.andExpect(jsonPath("data.categories[*].categoryName").value(
				containsInAnyOrder("스포츠/레저", "가구/인테리어")));
	}

}
