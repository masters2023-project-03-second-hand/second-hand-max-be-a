package codesquard.app.api.region;

import static codesquard.app.RegionTestSupport.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import codesquard.app.IntegrationTestSupport;
import codesquard.app.api.region.response.RegionListResponse;

class RegionQueryServiceTest extends IntegrationTestSupport {

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
