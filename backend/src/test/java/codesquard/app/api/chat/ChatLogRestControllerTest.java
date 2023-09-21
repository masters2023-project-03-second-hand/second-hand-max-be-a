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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import codesquard.app.ControllerTestSupport;
import codesquard.app.api.chat.request.ChatLogSendRequest;
import codesquard.app.api.chat.response.ChatLogSendResponse;
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
}
