package codesquard.app.api.item;

import static codesquard.app.CategoryTestSupport.*;
import static codesquard.app.ItemTestSupport.*;
import static codesquard.app.MemberTestSupport.*;
import static codesquard.app.domain.item.ItemStatus.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.any;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import codesquard.app.ControllerTestSupport;
import codesquard.app.api.errors.errorcode.ItemErrorCode;
import codesquard.app.api.errors.exception.NotFoundResourceException;
import codesquard.app.api.item.response.ItemDetailResponse;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.oauth.support.Principal;

@ActiveProfiles("test")
@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest extends ControllerTestSupport {

	private MockMvc mockMvc;

	@Autowired
	private ItemController itemController;

	@MockBean
	private ItemService itemService;

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(itemController)
			.setControllerAdvice(globalExceptionHandler)
			.setCustomArgumentResolvers(authPrincipalArgumentResolver)
			.alwaysDo(print())
			.build();

		given(authPrincipalArgumentResolver.supportsParameter(any())).willReturn(true);

		Principal principal = new Principal(1L, "23Yong@gmail.com", "23Yong", null, null);
		given(authPrincipalArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(principal);
	}

	@DisplayName("판매자가 자신이 판매하는 상품의 상세한 내용을 조회합니다.")
	@Test
	void findDetailItemBySeller() throws Exception {
		// given
		Member seller = createMember("avatarUrl", "23Yong@gmail.com", "23Yong");
		Category sport = findByName("스포츠/레저");
		Item item = createItem("빈티지 롤러 블레이드", "어린시절 추억의향수를 불러 일으키는 롤러 스케이트입니다.", 200000L, ON_SALE,
			"가락동", "thumbnailUrl", seller, sport);
		List<String> imageUrls = List.of("imageUrlValue1", "imageUrlValue2");

		ItemDetailResponse response = ItemDetailResponse.toBuyer(item, seller.getId(), imageUrls, false, null);
		given(itemService.findDetailItemBy(any(), any())).willReturn(response);

		// when & then
		mockMvc.perform(get("/api/items/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("statusCode").value(equalTo(200)))
			.andExpect(jsonPath("message").value(equalTo("상품 상세 조회에 성공하였습니다.")))
			.andExpect(jsonPath("data.isSeller").value(equalTo(true)))
			.andExpect(jsonPath("data.imageUrls").isArray())
			.andExpect(jsonPath("data.seller").value(equalTo("23Yong")))
			.andExpect(jsonPath("data.status").value(equalTo("판매중")))
			.andExpect(jsonPath("data.title").value(equalTo("빈티지 롤러 블레이드")))
			.andExpect(jsonPath("data.content").value(equalTo("어린시절 추억의향수를 불러 일으키는 롤러 스케이트입니다.")))
			.andExpect(jsonPath("data.categoryName").value(equalTo("스포츠/레저")))
			.andExpect(jsonPath("data.createdAt").exists())
			.andExpect(jsonPath("data.chatCount").value(equalTo(0)))
			.andExpect(jsonPath("data.wishCount").value(equalTo(0)))
			.andExpect(jsonPath("data.viewCount").value(equalTo(0)))
			.andExpect(jsonPath("data.price").value(equalTo(200000)))
			.andExpect(jsonPath("data.isInWishList").value(equalTo(false)));
	}

	@DisplayName("구매자가 상품의 상세한 내용을 조회합니다.")
	@Test
	void findDetailItemByBuyer() throws Exception {
		// given
		Member seller = createMember("avatarUrl", "23Yong@gmail.com", "23Yong");
		Category sport = findByName("스포츠/레저");
		Item item = createItem("빈티지 롤러 블레이드", "어린시절 추억의향수를 불러 일으키는 롤러 스케이트입니다.", 200000L, ON_SALE,
			"가락동", "thumbnailUrl", seller, sport);
		Long loginMemberId = 9999L;
		List<String> imageUrls = List.of("imageUrlValue1", "imageUrlValue2");

		ItemDetailResponse response = ItemDetailResponse.toSeller(item, loginMemberId, imageUrls, false);
		given(itemService.findDetailItemBy(any(), any())).willReturn(response);
		// when & then
		mockMvc.perform(get("/api/items/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("statusCode").value(equalTo(200)))
			.andExpect(jsonPath("message").value(equalTo("상품 상세 조회에 성공하였습니다.")))
			.andExpect(jsonPath("data.isSeller").value(equalTo(false)))
			.andExpect(jsonPath("data.imageUrls").isArray())
			.andExpect(jsonPath("data.seller").value(equalTo("23Yong")))
			.andExpect(jsonPath("data.status").value(equalTo("판매중")))
			.andExpect(jsonPath("data.title").value(equalTo("빈티지 롤러 블레이드")))
			.andExpect(jsonPath("data.content").value(equalTo("어린시절 추억의향수를 불러 일으키는 롤러 스케이트입니다.")))
			.andExpect(jsonPath("data.categoryName").value(equalTo("스포츠/레저")))
			.andExpect(jsonPath("data.createdAt").exists())
			.andExpect(jsonPath("data.chatCount").value(equalTo(0)))
			.andExpect(jsonPath("data.wishCount").value(equalTo(0)))
			.andExpect(jsonPath("data.viewCount").value(equalTo(0)))
			.andExpect(jsonPath("data.price").value(equalTo(200000)))
			.andExpect(jsonPath("data.isInWishList").value(equalTo(false)));
	}

	@DisplayName("구매자가 상품의 상세한 내용을 조회합니다.")
	@Test
	void findDetailItemWithNotExistItem() throws Exception {
		// given
		given(itemService.findDetailItemBy(any(), any()))
			.willThrow(new NotFoundResourceException(ItemErrorCode.ITEM_NOT_FOUND));

		// when & then
		mockMvc.perform(get("/api/items/9999"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("statusCode").value(equalTo(404)))
			.andExpect(jsonPath("message").value(equalTo("상품을 찾을 수 없습니다.")))
			.andExpect(jsonPath("data").value(equalTo(null)));
	}

	@DisplayName("상품을 삭제합니다.")
	@Test
	void deleteItem() throws Exception {
		// given
		willDoNothing().given(itemService).deleteItem(
			ArgumentMatchers.anyLong(),
			ArgumentMatchers.any(Principal.class));

		// when & then
		mockMvc.perform(delete("/api/items/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("statusCode").value(equalTo(200)))
			.andExpect(jsonPath("message").value(equalTo("상품 삭제가 완료되었습니다.")));
	}
}
