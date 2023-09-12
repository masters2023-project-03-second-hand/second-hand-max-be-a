package codesquard.app.api.membertown;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import codesquard.app.ControllerTestSupport;
import codesquard.app.api.errors.handler.GlobalExceptionHandler;
import codesquard.app.api.membertown.request.MemberAddRegionRequest;
import codesquard.app.api.membertown.response.MemberAddRegionResponse;
import codesquard.app.domain.membertown.MemberTown;
import codesquard.app.domain.oauth.support.AuthPrincipalArgumentResolver;
import codesquard.app.domain.oauth.support.Principal;

class MemberTownRestControllerTest extends ControllerTestSupport {

	private MockMvc mockMvc;

	@MockBean
	private AuthPrincipalArgumentResolver authPrincipalArgumentResolver;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(new MemberTownRestController(memberTownService))
			.setControllerAdvice(new GlobalExceptionHandler())
			.setCustomArgumentResolvers(authPrincipalArgumentResolver)
			.alwaysDo(print())
			.build();
	}

	@DisplayName("전체 주소와 동 주소를 가지고 회원 동네를 추가한다")
	@Test
	public void addMemberTown() throws Exception {
		// given
		MemberAddRegionRequest request = MemberAddRegionRequest.create("서울 송파구 가락동", "가락동");
		MemberAddRegionResponse response = MemberAddRegionResponse.create(MemberTown.create("가락동"));
		given(memberTownService.addMemberTown(
			ArgumentMatchers.any(Principal.class),
			ArgumentMatchers.any(MemberAddRegionRequest.class)))
			.willReturn(response);

		// when & null
		mockMvc.perform(post("/api/regions")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("statusCode").value(equalTo(201)))
			.andExpect(jsonPath("message").value(equalTo("동네 추가에 성공하였습니다.")))
			.andExpect(jsonPath("data").value(equalTo(null)));
	}

}
