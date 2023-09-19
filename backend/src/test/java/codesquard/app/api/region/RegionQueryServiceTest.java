package codesquard.app.api.region;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import codesquard.app.IntegrationTestSupport;
import codesquard.app.api.region.response.RegionListResponse;
import codesquard.app.domain.region.RegionFixedFactory;

class RegionQueryServiceTest extends IntegrationTestSupport {

	@DisplayName("주소 목록을 처음 조회할때 10개가 조회한다")
	@Test
	public void findAllByRegionName() {
		// given
		regionRepository.saveAll(RegionFixedFactory.createFixedRegions());
		int size = 10;
		Long cursor = null;
		String region = null;

		// when
		RegionListResponse response = regionQueryService.searchBySlice(size, cursor, region);

		// then
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(response.getContents()).hasSize(10);
			softAssertions.assertThat(response.getPaging().getNextCursor()).isNotNull();
			softAssertions.assertThat(response.getPaging().isHasNext()).isEqualTo(true);
			softAssertions.assertAll();
		});
	}
}
