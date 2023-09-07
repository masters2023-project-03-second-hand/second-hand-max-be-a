package codesquard.app.api.item;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import codesquard.app.ControllerTestSupport;
import codesquard.app.api.category.CategoryFixedFactory;
import codesquard.app.api.errors.errorcode.ItemErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.errors.handler.GlobalExceptionHandler;
import codesquard.app.api.item.response.ItemDetailResponse;
import codesquard.app.api.oauth.OauthFixedFactory;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.image.Image;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.oauth.support.AuthPrincipalArgumentResolver;
import codesquard.app.domain.oauth.support.Principal;

class ItemControllerTest extends ControllerTestSupport {

	private MockMvc mockMvc;

	@MockBean
	private AuthPrincipalArgumentResolver authPrincipalArgumentResolver;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(new ItemController(itemQueryService, itemService))
			.setControllerAdvice(new GlobalExceptionHandler())
			.setCustomArgumentResolvers(authPrincipalArgumentResolver)
			.alwaysDo(print())
			.build();

		Principal principal = new Principal(1L, "dragonbead95@naver.com", "bruni", null, null);
		when(authPrincipalArgumentResolver.supportsParameter(any())).thenReturn(true);
		when(authPrincipalArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(principal);
	}

	@DisplayName("판매자가 자신이 판매하는 상품의 상세한 내용을 조회합니다.")
	@Test
	public void findDetailItemBySeller() throws Exception {
		// given
		long itemId = 1L;
		Member seller = OauthFixedFactory.createFixedMemberWithMemberTown();
		Category category = CategoryFixedFactory.createdFixedCategory();
		List<Image> images = ImageFixedFactory.createFixedImages();
		Item item = ItemFixedFactory.createFixedItem(seller, category, images, new ArrayList<>(), 0L);
		ItemDetailResponse response = ItemDetailResponse.createWithSellerResponse(item, seller);
		Mockito.when(itemQueryService.findDetailItemBy(any(), any())).thenReturn(response);
		// when & then
		mockMvc.perform(get("/api/items/" + itemId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("statusCode").value(equalTo(200)))
			.andExpect(jsonPath("message").value(equalTo("상품 상세 조회에 성공하였습니다.")))
			.andExpect(jsonPath("data.isSeller").value(equalTo(true)))
			.andExpect(jsonPath("data.imageUrls").isArray())
			.andExpect(jsonPath("data.seller").value(equalTo("23Yong")))
			.andExpect(jsonPath("data.status").value(equalTo("판매중")))
			.andExpect(jsonPath("data.title").value(equalTo("빈티지 롤러 스케이트")))
			.andExpect(jsonPath("data.content").value(equalTo("어린시절 추억의향수를 불러 일으키는 롤러 스케이트입니다.")))
			.andExpect(jsonPath("data.categoryName").value(equalTo("가구/인테리어")))
			.andExpect(jsonPath("data.createdAt").exists())
			.andExpect(jsonPath("data.chatCount").value(equalTo(0)))
			.andExpect(jsonPath("data.wishCount").value(equalTo(0)))
			.andExpect(jsonPath("data.viewCount").value(equalTo(0)))
			.andExpect(jsonPath("data.price").value(equalTo(169000)));
	}

	@DisplayName("구매자가 상품의 상세한 내용을 조회합니다.")
	@Test
	public void findDetailItemByBuyer() throws Exception {
		// given
		long itemId = 1L;
		Member seller = OauthFixedFactory.createFixedMemberWithMemberTown();
		Category category = CategoryFixedFactory.createdFixedCategory();
		List<Image> images = ImageFixedFactory.createFixedImages();
		Item item = ItemFixedFactory.createFixedItem(seller, category, images, new ArrayList<>(), 0L);
		ItemDetailResponse response = ItemDetailResponse.createWithBuyerResponse(item, seller);
		Mockito.when(itemQueryService.findDetailItemBy(any(), any())).thenReturn(response);
		// when & then
		mockMvc.perform(get("/api/items/" + itemId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("statusCode").value(equalTo(200)))
			.andExpect(jsonPath("message").value(equalTo("상품 상세 조회에 성공하였습니다.")))
			.andExpect(jsonPath("data.isSeller").value(equalTo(false)))
			.andExpect(jsonPath("data.imageUrls").isArray())
			.andExpect(jsonPath("data.seller").value(equalTo("23Yong")))
			.andExpect(jsonPath("data.status").value(equalTo("판매중")))
			.andExpect(jsonPath("data.title").value(equalTo("빈티지 롤러 스케이트")))
			.andExpect(jsonPath("data.content").value(equalTo("어린시절 추억의향수를 불러 일으키는 롤러 스케이트입니다.")))
			.andExpect(jsonPath("data.categoryName").value(equalTo("가구/인테리어")))
			.andExpect(jsonPath("data.createdAt").exists())
			.andExpect(jsonPath("data.chatCount").value(equalTo(0)))
			.andExpect(jsonPath("data.wishCount").value(equalTo(0)))
			.andExpect(jsonPath("data.viewCount").value(equalTo(0)))
			.andExpect(jsonPath("data.price").value(equalTo(169000)));
	}

	@DisplayName("구매자가 상품의 상세한 내용을 조회합니다.")
	@Test
	public void findDetailItemWithNotExistItem() throws Exception {
		// given
		long itemId = 9999L;
		Mockito.when(itemQueryService.findDetailItemBy(any(), any()))
			.thenThrow(new RestApiException(ItemErrorCode.ITEM_NOT_FOUND));
		// when & then
		mockMvc.perform(get("/api/items/" + itemId))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("statusCode").value(equalTo(404)))
			.andExpect(jsonPath("message").value(equalTo("상품을 찾을 수 없습니다.")))
			.andExpect(jsonPath("data").value(equalTo(null)));
	}
}
