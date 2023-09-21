package codesquard.app.api.chat;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;

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
import codesquard.app.api.chat.response.ChatRoomCreateResponse;
import codesquard.app.domain.oauth.support.Principal;

@ActiveProfiles("test")
@WebMvcTest(controllers = ChatRoomRestController.class)
class ChatRoomRestControllerTest extends ControllerTestSupport {

	private MockMvc mockMvc;

	@Autowired
	private ChatRoomRestController chatRoomRestController;

	@MockBean
	private ChatRoomService chatRoomService;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(chatRoomRestController)
			.setControllerAdvice(globalExceptionHandler)
			.setCustomArgumentResolvers(authPrincipalArgumentResolver)
			.alwaysDo(print())
			.build();

		given(authPrincipalArgumentResolver.supportsParameter(any())).willReturn(true);

		Principal principal = new Principal(1L, "23Yong@gmail.com", "23Yong", null, null);
		given(authPrincipalArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(principal);
	}

	@DisplayName("채팅방 생성을 요청한다")
	@Test
	public void createChatRoom() throws Exception {
		// given
		Map<String, Object> responseBody = new HashMap<>();
		responseBody.put("id", 1L);

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
			.andExpect(jsonPath("data.id").value(equalTo(1)));
	}
}
