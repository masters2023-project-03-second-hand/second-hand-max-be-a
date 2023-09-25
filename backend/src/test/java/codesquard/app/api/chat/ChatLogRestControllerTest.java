package codesquard.app.api.chat;

import static codesquard.app.MemberTestSupport.*;
import static codesquard.app.domain.item.ItemStatus.*;
import static java.nio.charset.StandardCharsets.*;
import static java.time.LocalDateTime.*;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.AsyncListener;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockAsyncContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import codesquard.app.CategoryTestSupport;
import codesquard.app.ControllerTestSupport;
import codesquard.app.api.chat.request.ChatLogSendRequest;
import codesquard.app.api.chat.response.ChatLogItemResponse;
import codesquard.app.api.chat.response.ChatLogListResponse;
import codesquard.app.api.chat.response.ChatLogMessageResponse;
import codesquard.app.api.chat.response.ChatLogSendResponse;
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

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(chatLogRestController)
			.setControllerAdvice(globalExceptionHandler)
			.setCustomArgumentResolvers(authPrincipalArgumentResolver)
			.alwaysDo(print())
			.build();

		given(authPrincipalArgumentResolver.supportsParameter(any())).willReturn(true);

		Principal principal = new Principal(1L, "23Yong@gmail.com", "23Yong", null, null);
		given(authPrincipalArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(principal);
	}

	@DisplayName("구매자가 판매자에게 채팅 메시지를 전송합니다.")
	@Test
	public void sendMessage() throws Exception {
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
			ArgumentMatchers.any(ChatLogSendRequest.class),
			ArgumentMatchers.anyLong(),
			ArgumentMatchers.any(Principal.class)))
			.willReturn(response);

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
	public void readMessages() throws Exception {
		// given
		Member seller = createMember("avatarUrl", "carlynne@naver.com", "carlynne");
		Member buyer = createMember("avatarUrl", "23Yong@gmail.com", "23Yong");
		Item item = Item.builder()
			.title("빈티지 롤러 블레이드")
			.content("어린시절 추억의향수를 불러 일으키는 롤러 스케이트입니다.")
			.price(200000L)
			.status(ON_SALE)
			.region("가락동")
			.createdAt(now())
			.wishCount(0L)
			.viewCount(0L)
			.chatCount(0L)
			.thumbnailUrl("thumbnailUrl")
			.member(seller)
			.category(CategoryTestSupport.findByName("스포츠/레저"))
			.build();
		ChatLogItemResponse itemResponse = ChatLogItemResponse.from(item);
		ChatRoom chatRoom = new ChatRoom(buyer, item);
		ChatLog chatLog = new ChatLog("안녕하세요. 롤러블레이브를 사고 싶습니다. 만원만 깍아주세요.", "23Yong", "carlynne", chatRoom);
		ChatLogMessageResponse messageResponse = ChatLogMessageResponse.from(0, chatLog, Principal.from(buyer));
		ChatLogListResponse response = new ChatLogListResponse("carlynne", itemResponse, List.of(messageResponse));
		given(chatLogService.readMessages(
			ArgumentMatchers.anyLong(),
			ArgumentMatchers.anyInt(),
			ArgumentMatchers.any(Principal.class)
		)).willReturn(response);

		int chatRoomId = 1;
		// when
		MvcResult asyncListener = mockMvc.perform(get("/api/chats/" + chatRoomId))
			.andExpect(request().asyncStarted())
			.andReturn();

		String contentAsString = mockMvc.perform(asyncDispatch(asyncListener))
			.andReturn()
			.getResponse()
			.getContentAsString(UTF_8);

		ChatLogListResponse chatLogListResponse = objectMapper.readValue(contentAsString, ChatLogListResponse.class);

		//then
		assertAll(
			() -> assertThat(chatLogListResponse.getChatPartnerName()).isEqualTo("carlynne"),
			() -> assertThat(chatLogListResponse.getItem().getTitle()).isEqualTo("빈티지 롤러 블레이드"),
			() -> assertThat(chatLogListResponse.getItem().getThumbnailUrl()).isEqualTo("thumbnailUrl"),
			() -> assertThat(chatLogListResponse.getItem().getPrice()).isEqualTo(200000),
			() -> assertThat(chatLogListResponse.getChat()).hasSize(1),
			() -> assertThat(chatLogListResponse.getChat())
				.extracting("messageIndex", "message", "me")
				.containsExactlyInAnyOrder(Tuple.tuple(0, "안녕하세요. 롤러블레이브를 사고 싶습니다. 만원만 깍아주세요.", false))
		);
	}

	@DisplayName("회원은 채팅 메시지 목록 요청시 messageIndex를 음수로 보낼수 없다")
	@Test
	public void readMessagesWithNegativeMessageIndex() throws Exception {
		// given
		int chatRoomId = 1;
		String messageIndex = String.valueOf(-1);
		// when & then
		mockMvc.perform(get("/api/chats/" + chatRoomId)
				.param("messageIndex", messageIndex))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("statusCode").value(equalTo(400)))
			.andExpect(jsonPath("message").value(equalTo("유효하지 않은 입력형식입니다.")))
			.andExpect(jsonPath("data[*].field").value(containsInAnyOrder("readMessages.messageIndex")))
			.andExpect(jsonPath("data[*].defaultMessage").value(containsInAnyOrder("messageIndex는 0 이상이어야 합니다.")));
	}

	@DisplayName("회원이 새로운 채팅 메시지를 요청했지만 새로운 메시지가 없다는 응답을 받는다")
	@Test
	public void readMessagesWithTimeout() throws Exception {
		// given
		ChatLogItemResponse itemResponse = null;
		ChatLogListResponse response = new ChatLogListResponse("carlynne", itemResponse, List.of());
		given(chatLogService.readMessages(
			ArgumentMatchers.anyLong(),
			ArgumentMatchers.anyInt(),
			ArgumentMatchers.any(Principal.class)
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
			.andExpect(jsonPath("statusCode").value(equalTo(408)))
			.andExpect(jsonPath("message").value(equalTo("새로운 채팅 메시지가 존재하지 않습니다.")));
	}
}
