package codesquard.app.api.member;

import static codesquard.app.MemberTestSupport.*;
import static codesquard.app.MemberTownTestSupport.*;
import static codesquard.app.RegionTestSupport.*;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import codesquard.app.ControllerTestSupport;
import codesquard.app.api.membertown.MemberTownService;
import codesquard.app.api.membertown.response.MemberTownItemResponse;
import codesquard.app.api.membertown.response.MemberTownListResponse;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.membertown.MemberTown;
import codesquard.app.domain.oauth.support.Principal;
import codesquard.app.domain.region.Region;

@WebMvcTest(controllers = MemberController.class)
class MemberControllerTest extends ControllerTestSupport {

	private MockMvc mockMvc;

	@Autowired
	private MemberController memberController;

	@MockBean
	private MemberTownService memberTownService;

	@MockBean
	private MemberService memberService;

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(memberController)
			.setControllerAdvice(globalExceptionHandler)
			.setCustomArgumentResolvers(authPrincipalArgumentResolver)
			.alwaysDo(print())
			.build();

		given(authPrincipalArgumentResolver.supportsParameter(any())).willReturn(true);

		Principal principal = new Principal(1L, "23Yong@gmail.com", "23Yong", null, null);
		given(authPrincipalArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(principal);
	}

	@DisplayName("회원은 회원의 동네를 조회할 수 있다")
	@Test
	void readALlMemberTowns() throws Exception {
		// given
		Member member = createMember("avatarUrl", "23Yong@gmail.com", "23Yong");
		Region region = createRegion("서울 송파구 가락동");
		MemberTown memberTown = createMemberTown(member, region, true);
		List<MemberTownItemResponse> itemResponses = List.of(MemberTownItemResponse.from(memberTown));
		MemberTownListResponse response = new MemberTownListResponse(itemResponses);
		given(memberTownService.readAll(
			ArgumentMatchers.any(Principal.class)))
			.willReturn(response);

		// when & then
		mockMvc.perform(get("/api/members/regions"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("statusCode").value(equalTo(200)))
			.andExpect(jsonPath("message").value(equalTo("회원 동네 목록 조회를 완료하였습니다.")))
			.andExpect(jsonPath("data.addresses[*].fullAddressName").value(containsInAnyOrder("서울 송파구 가락동")))
			.andExpect(jsonPath("data.addresses[*].addressName").value(containsInAnyOrder("가락동")))
			.andExpect(jsonPath("data.addresses[*].isSelected").value(containsInAnyOrder(true)));

	}
}
