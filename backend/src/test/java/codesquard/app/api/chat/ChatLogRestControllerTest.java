package codesquard.app.api.chat;

import static codesquard.app.CategoryTestSupport.*;
import static codesquard.app.ItemTestSupport.*;
import static codesquard.app.MemberTestSupport.*;
import static codesquard.app.domain.item.ItemStatus.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.AsyncListener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockAsyncContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import codesquard.app.ControllerTestSupport;
import codesquard.app.api.chat.request.ChatLogSendRequest;
import codesquard.app.api.chat.response.ChatLogItemResponse;
import codesquard.app.api.chat.response.ChatLogListResponse;
import codesquard.app.api.chat.response.ChatLogMessageResponse;
import codesquard.app.api.chat.response.ChatLogSendResponse;
import codesquard.app.api.member.MemberService;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.chat.ChatLog;
import codesquard.app.domain.chat.ChatRoom;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.oauth.support.Principal;

@ActiveProfiles("test")
@WebMvcTest(controllers = ChatLogRestController.class)
class ChatLogRestControllerTest extends ControllerTestSupport {

	private MockMvc mockMvc;

	@Autowired
	private ChatLogRestController chatLogRestController;

	@MockBean
	private ChatLogService chatLogService;

	@MockBean
	private ChatService chatService;

	@MockBean
	private MemberService memberService;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver;

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(chatLogRestController)
			.setControllerAdvice(globalExceptionHandler)
			.setCustomArgumentResolvers(pageableHandlerMethodArgumentResolver, authPrincipalArgumentResolver)
			.alwaysDo(print())
			.build();

		given(authPrincipalArgumentResolver.supportsParameter(any())).willReturn(true);

		Principal principal = new Principal(1L, "23Yong@gmail.com", "23Yong", null, null);
		given(authPrincipalArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(principal);
	}

	@DisplayName("구매자가 판매자에게 채팅 메시지를 전송합니다.")
	@Test
	void sendMessage() throws Exception {
		// given
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("message", "롤러 블레이드 삽니다.");

		Map<String, Object> responseBody = new HashMap<>();
		responseBody.put("id", 1L);
		responseBody.put("message", "롤러 블레이드 삽니다.");
		responseBody.put("sender", "23Yong");
		responseBody.put("receiver", "bruni");

		ChatLogSendResponse response = objectMapper.readValue(objectMapper.writeValueAsString(responseBody),
			ChatLogSendResponse.class);

		given(chatLogService.sendMessage(
			any(ChatLogSendRequest.class),
			anyLong(),
			any(Principal.class)))
			.willReturn(response);

		Member receiver = createMember("avatarUrl", "bruni@gmail.com", "bruni");
		given(memberService.findMemberByLoginId(
			anyString()
		)).willReturn(receiver);

		// when & then
		mockMvc.perform(post("/api/chats/1")
				.content(objectMapper.writeValueAsString(requestBody))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("statusCode").value(equalTo(201)))
			.andExpect(jsonPath("message").value(equalTo("메시지 전송이 완료되었습니다.")))
			.andExpect(jsonPath("data.id").value(equalTo(1)))
			.andExpect(jsonPath("data.message").value(equalTo("롤러 블레이드 삽니다.")))
			.andExpect(jsonPath("data.sender").value(equalTo("23Yong")))
			.andExpect(jsonPath("data.receiver").value(equalTo("bruni")));
	}

	@DisplayName("회원은 채팅방 안에 채팅 메시지 목록들을 요청합니다.")
	@Test
	void readMessages() throws Exception {
		// given
		Member seller = createMember("avatarUrl", "carlynne@naver.com", "carlynne");
		Member buyer = createMember("avatarUrl", "23Yong@gmail.com", "23Yong");
		Category sport = findByName("스포츠/레저");
		Item item = createItem("빈티지 롤러 블레이드", "어린시절 추억의향수를 불러 일으키는 롤러 스케이트입니다.", 200000L, ON_SALE,
			"가락동", "thumbnailUrl", seller, sport);

		ChatLogItemResponse itemResponse = ChatLogItemResponse.from(item);
		ChatRoom chatRoom = new ChatRoom(buyer, item);
		ChatLog chatLog = new ChatLog("안녕하세요. 롤러블레이브를 사고 싶습니다. 만원만 깍아주세요.", "23Yong", "carlynne", chatRoom, 1);
		ChatLogMessageResponse messageResponse = ChatLogMessageResponse.from(chatLog, Principal.from(buyer));
		ChatLogListResponse response = new ChatLogListResponse("carlynne", itemResponse, List.of(messageResponse),
			null);
		given(chatLogService.readMessages(anyLong(), any(Principal.class), anyLong()))
			.willReturn(response);
		int chatRoomId = 1;

		// when
		MvcResult asyncListener = mockMvc.perform(get("/api/chats/" + chatRoomId))
			.andExpect(request().asyncStarted())
			.andReturn();

		// then
		mockMvc.perform(asyncDispatch(asyncListener))
			.andExpect(status().isOk())
			.andExpect(jsonPath("statusCode").value(equalTo(200)))
			.andExpect(jsonPath("message").value(equalTo("채팅 메시지 목록 조회가 완료되었습니다.")))
			.andExpect(jsonPath("data.chatPartnerName").value(equalTo("carlynne")))
			.andExpect(jsonPath("data.item.title").value(equalTo("빈티지 롤러 블레이드")))
			.andExpect(jsonPath("data.item.thumbnailUrl").value(equalTo("thumbnailUrl")))
			.andExpect(jsonPath("data.item.price").value(equalTo(200000)))
			.andExpect(jsonPath("data.chat[*].message").value(containsInAnyOrder("안녕하세요. 롤러블레이브를 사고 싶습니다. 만원만 깍아주세요.")))
			.andExpect(jsonPath("data.chat[*].isMe").value(containsInAnyOrder(true)));
	}

	@DisplayName("회원이 새로운 채팅 메시지를 요청했지만 새로운 메시지가 없다는 응답을 받는다")
	@Test
	void readMessagesWithTimeout() throws Exception {
		// given
		ChatLogItemResponse itemResponse = null;
		ChatLogListResponse response = new ChatLogListResponse("carlynne", itemResponse, List.of(), null);
		given(chatLogService.readMessages(
			anyLong(),
			any(Principal.class),
			anyLong()
		)).willReturn(response);
		int chatRoomId = 1;

		// when
		MvcResult asyncListener = mockMvc.perform(get("/api/chats/" + chatRoomId))
			.andExpect(request().asyncStarted())
			.andExpect(status().isOk())
			.andReturn();

		MockAsyncContext ctx = (MockAsyncContext)asyncListener.getRequest().getAsyncContext();
		for (AsyncListener listener : Objects.requireNonNull(ctx).getListeners()) {
			listener.onTimeout(null);
		}
		// then
		mockMvc.perform(asyncDispatch(asyncListener))
			.andExpect(status().isOk())
			.andExpect(jsonPath("statusCode").value(equalTo(200)))
			.andExpect(jsonPath("message").value(equalTo("새로운 채팅 메시지가 존재하지 않습니다.")))
			.andExpect(jsonPath("data").value(equalTo(Collections.emptyList())));
	}
}
