package codesquard.app.api.chat;

import static codesquard.app.CategoryTestSupport.*;
import static codesquard.app.ItemTestSupport.*;
import static codesquard.app.MemberTestSupport.*;
import static codesquard.app.domain.item.ItemStatus.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import codesquard.app.ControllerTestSupport;
import codesquard.app.api.chat.response.ChatRoomCreateResponse;
import codesquard.app.api.chat.response.ChatRoomItemResponse;
import codesquard.app.api.chat.response.ChatRoomListResponse;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.chat.ChatLog;
import codesquard.app.domain.chat.ChatRoom;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.oauth.support.Principal;

@ActiveProfiles("test")
@WebMvcTest(controllers = ChatRoomRestController.class)
class ChatRoomRestControllerTest extends ControllerTestSupport {

	private MockMvc mockMvc;

	@Autowired
	private ChatRoomRestController chatRoomRestController;

	@MockBean
	private ChatRoomService chatRoomService;

	@MockBean
	private ChatService chatService;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver;

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(chatRoomRestController)
			.setControllerAdvice(globalExceptionHandler)
			.setCustomArgumentResolvers(pageableHandlerMethodArgumentResolver, authPrincipalArgumentResolver)
			.alwaysDo(print())
			.build();

		given(authPrincipalArgumentResolver.supportsParameter(any())).willReturn(true);

		Principal principal = new Principal(1L, "23Yong@gmail.com", "23Yong", null, null);
		given(authPrincipalArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(principal);
	}

	@DisplayName("채팅방 생성을 요청한다")
	@Test
	void createChatRoom() throws Exception {
		// given
		Map<String, Object> responseBody = new HashMap<>();
		responseBody.put("chatRoomId", 1L);

		ChatRoomCreateResponse response = objectMapper.readValue(objectMapper.writeValueAsString(responseBody),
			ChatRoomCreateResponse.class);

		given(chatRoomService.createChatRoom(
			ArgumentMatchers.anyLong(),
			ArgumentMatchers.any(Principal.class)))
			.willReturn(response);

		// when & then
		mockMvc.perform(post("/api/items/1/chats"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("statusCode").value(equalTo(201)))
			.andExpect(jsonPath("message").value(equalTo("채팅방 생성을 완료하였습니다.")))
			.andExpect(jsonPath("data.chatRoomId").value(equalTo(1)));
	}

	@DisplayName("회원은 채팅방 목록을 요청한다")
	@Test
	void readAllChatRoom() throws Exception {
		// given
		Member seller = createMember("avatarUrl", "carlynne@naver.com", "carlynne");
		Member buyer = createMember("avatarUrlValue", "carlynne@naver.com", "carlynne");

		Category sport = findByName("스포츠/레저");
		Item item = createItem("빈티지 롤러 블레이드", "어린시절 추억의향수를 불러 일으키는 롤러 스케이트입니다.", 200000L, ON_SALE,
			"가락동", "thumbnailUrl", seller, sport);

		ChatRoom chatRoom = new ChatRoom(buyer, item);
		ChatLog chatLog = new ChatLog("안녕하세요. 롤러블레이브를 사고 싶습니다. 만원만 깍아주세요.", "carlynne", "23Yong", chatRoom, 1);
		Long newMessageCount = 1L;

		List<ChatRoomItemResponse> contents = List.of(
			ChatRoomItemResponse.of(chatRoom, item, buyer, chatLog, newMessageCount));
		ChatRoomListResponse response = new ChatRoomListResponse(contents, false, null);
		given(chatRoomService.readAllChatRoom(
			any(Principal.class),
			any(Pageable.class)))
			.willReturn(response);

		// when & then
		mockMvc.perform(get("/api/chats"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("statusCode").value(equalTo(200)))
			.andExpect(jsonPath("message").value(equalTo("채팅방 목록 조회를 완료하였습니다.")))
			.andExpect(jsonPath("data.contents[*].thumbnailUrl").value(containsInAnyOrder("thumbnailUrl")))
			.andExpect(jsonPath("data.contents[*].chatPartnerName").value(containsInAnyOrder("carlynne")))
			.andExpect(jsonPath("data.contents[*].chatPartnerProfile").value(containsInAnyOrder("avatarUrlValue")))
			.andExpect(jsonPath("data.contents[*].lastSendMessage").value(
				containsInAnyOrder("안녕하세요. 롤러블레이브를 사고 싶습니다. 만원만 깍아주세요.")))
			.andExpect(jsonPath("data.contents[*].newMessageCount").value(containsInAnyOrder(1)));
	}
}
