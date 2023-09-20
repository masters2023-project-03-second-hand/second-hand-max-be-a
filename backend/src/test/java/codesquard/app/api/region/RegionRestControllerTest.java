package codesquard.app.api.region;

import static codesquard.app.RegionTestSupport.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import codesquard.app.ControllerTestSupport;
import codesquard.app.api.region.response.RegionItemResponse;
import codesquard.app.api.region.response.RegionListResponse;

@ActiveProfiles("test")
@WebMvcTest(controllers = RegionRestController.class)
class RegionRestControllerTest extends ControllerTestSupport {

	private MockMvc mockMvc;

	@Autowired
	private RegionRestController regionRestController;

	@MockBean
	private RegionService regionService;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(regionRestController)
			.setControllerAdvice(globalExceptionHandler)
			.alwaysDo(print())
			.build();
	}

	@DisplayName("동네(지역) 목록을 조회한다")
	@Test
	public void findAll() throws Exception {
		// given
		RegionItemResponse region1 = createRegionItemResponse("경기 부천시 괴안동");
		RegionItemResponse region2 = createRegionItemResponse("경기 부천시 범박동");
		RegionItemResponse region3 = createRegionItemResponse("경기 부천시 범박동");
		RegionListResponse response = new RegionListResponse(List.of(region1, region2, region3), true, 3L);

		given(regionService.searchBySlice(anyInt(), anyLong(), anyString()))
			.willReturn(response);

		// when & then
		mockMvc.perform(get("/api/regions")
				.param("size", "3")
				.param("cursor", "3")
				.param("region", "부천시"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("statusCode").value(equalTo(200)))
			.andExpect(jsonPath("message").value(equalTo("주소 목록 조회에 성공하였습니다.")))
			.andExpect(jsonPath("data.contents").isArray())
			.andExpect(jsonPath("data.paging.nextCursor").value(3))
			.andExpect(jsonPath("data.paging.hasNext").value(true));
	}

}
