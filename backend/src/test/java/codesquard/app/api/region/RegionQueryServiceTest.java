package codesquard.app.api.region;

import static codesquard.app.RegionTestSupport.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import codesquard.app.api.region.response.RegionListResponse;
import codesquard.app.domain.region.RegionRepository;

@ActiveProfiles("test")
@SpringBootTest
class RegionQueryServiceTest {

	@Autowired
	private RegionRepository regionRepository;

	@Autowired
	private RegionQueryService regionQueryService;

	@AfterEach
	void tearDown() {
		regionRepository.deleteAllInBatch();
	}

	@DisplayName("주소 목록을 처음 조회할때 10개가 조회한다")
	@Test
	public void findAllByRegionName() {
		// given
		regionRepository.saveAll(createFixedRegions());
		int size = 10;
		Long cursor = null;
		String region = null;

		// when
		RegionListResponse response = regionQueryService.searchBySlice(size, cursor, region);

		// then
		assertAll(() -> {
			assertThat(response.getContents()).hasSize(10);
			assertThat(response.getPaging().getNextCursor()).isNotNull();
			assertThat(response.getPaging().isHasNext()).isEqualTo(true);
		});
	}
}
