package codesquard.app.api.membertown;

import static codesquard.app.MemberTestSupport.*;
import static org.hamcrest.Matchers.*;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import codesquard.app.ControllerTestSupport;
import codesquard.app.api.membertown.request.MemberTownAddRequest;
import codesquard.app.api.membertown.request.MemberTownRemoveRequest;
import codesquard.app.api.membertown.response.MemberAddRegionResponse;
import codesquard.app.api.membertown.response.MemberTownRemoveResponse;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.membertown.MemberTown;
import codesquard.app.domain.oauth.support.Principal;
import codesquard.app.domain.region.Region;

@WebMvcTest(controllers = MemberTownRestController.class)
class MemberTownRestControllerTest extends ControllerTestSupport {

	private MockMvc mockMvc;

	@Autowired
	private MemberTownRestController memberTownRestController;

	@MockBean
	private MemberTownService memberTownService;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(memberTownRestController)
			.setControllerAdvice(globalExceptionHandler)
			.setCustomArgumentResolvers(authPrincipalArgumentResolver)
			.alwaysDo(print())
			.build();
	}

	@DisplayName("주소 등록번호를 가지고 회원 동네를 추가한다")
	@Test
	public void addMemberTown() throws Exception {
		// given
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("addressId", 1L);

		Member member = createMember("avatarUrl", "23Yong@gmail.com", "23Yong");
		Region region = new Region("서울 송파구 가락동");
		MemberTown memberTown = new MemberTown(region.getShortAddress(), member, region);
		MemberAddRegionResponse response = MemberAddRegionResponse.from(memberTown);

		given(memberTownService.addMemberTown(
			ArgumentMatchers.any(Principal.class),
			ArgumentMatchers.any(MemberTownAddRequest.class)))
			.willReturn(response);

		// when & then
		mockMvc.perform(post("/api/regions")
				.content(objectMapper.writeValueAsString(requestBody))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("statusCode").value(equalTo(201)))
			.andExpect(jsonPath("message").value(equalTo("동네 추가에 성공하였습니다.")))
			.andExpect(jsonPath("data").value(equalTo(null)));
	}

	@DisplayName("주소 등록번호를 가지고 회원의 동네를 삭제한다")
	@Test
	public void removeMemberTown() throws Exception {
		// given
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("addressId", 1L);
		MemberTownRemoveResponse response = MemberTownRemoveResponse.create("서울 송파구 가락동");
		given(memberTownService.removeMemberTown(
			ArgumentMatchers.any(Principal.class),
			ArgumentMatchers.any(MemberTownRemoveRequest.class)))
			.willReturn(response);

		// when & then
		mockMvc.perform(delete("/api/regions")
				.content(objectMapper.writeValueAsString(requestBody))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("statusCode").value(equalTo(200)))
			.andExpect(jsonPath("message").value(equalTo("동네 삭제에 성공하였습니다.")))
			.andExpect(jsonPath("data").value(equalTo(null)));
	}

	@DisplayName("주소 등록번호가 null이면 회원의 동네를 제거할 수 없다")
	@Test
	public void removeMemberTownWithAddressIsNull() throws Exception {
		// given
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("addressId", null);

		// when & then
		mockMvc.perform(delete("/api/regions")
				.content(objectMapper.writeValueAsString(requestBody))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("statusCode").value(equalTo(400)))
			.andExpect(jsonPath("message").value(equalTo("유효하지 않은 입력형식입니다.")))
			.andExpect(jsonPath("data[*].field").value(containsInAnyOrder("addressId")))
			.andExpect(jsonPath("data[*].defaultMessage")
				.value(containsInAnyOrder("주소 정보는 필수 정보입니다.")));
	}
}
