package codesquard.app.domain.region;

import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import codesquard.app.IntegrationTestSupport;

class RegionPaginationRepositoryTest extends IntegrationTestSupport {

	private static final Logger log = LoggerFactory.getLogger(RegionPaginationRepositoryTest.class);

	@DisplayName("아무 조건없이 모든 동네중 10개를 조회한다")
	@Test
	public void searchBySlice() {
		// given
		List<Region> regions = RegionFixedFactory.createFixedRegions();
		regionRepository.saveAll(regions);

		Long lastRegionId = null;
		Pageable pageable = PageRequest.ofSize(10);
		String regionName = null;
		// when
		Slice<Region> regionSlice = regionPaginationRepository.searchBySlice(lastRegionId, regionName, pageable);
		// then
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(regionSlice.getContent()).hasSize(10);
			softAssertions.assertThat(regionSlice.hasNext()).isTrue();
			softAssertions.assertAll();
		});
	}

	@DisplayName("첫 동네 조회 이후 5칸 스크롤하여 다음 페이지의 동네를 조회한다")
	@Test
	public void searchBySliceWithScrollToNextPage() {
		// given
		List<Region> regions = RegionFixedFactory.createFixedRegions();
		regionRepository.saveAll(regions);

		Long lastRegionId = null;
		String regionName = null;
		Pageable pageable = PageRequest.ofSize(10);
		Slice<Region> firstSlice = regionPaginationRepository.searchBySlice(lastRegionId, regionName, pageable);

		lastRegionId = getNextCursor(firstSlice);
		pageable = PageRequest.ofSize(5);
		// when
		Slice<Region> regionSlice = regionPaginationRepository.searchBySlice(lastRegionId, regionName, pageable);
		// then
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(regionSlice.getContent()).hasSize(5);
			softAssertions.assertThat(regionSlice.hasNext()).isTrue();
			softAssertions.assertAll();
		});
	}

	@DisplayName("동네 이름에 '부천시'가 들어간 조회 이후 5칸 만큼 스크롤한다")
	@Test
	public void searchBySliceRegionNameWithScrollToNextPage() {
		// given
		List<Region> regions = RegionFixedFactory.createFixedRegions();
		regionRepository.saveAll(regions);

		Long lastRegionId = null;
		String regionName = "부천시";
		Pageable pageable = PageRequest.ofSize(10);
		Slice<Region> firstSlice = regionPaginationRepository.searchBySlice(lastRegionId, regionName, pageable);

		lastRegionId = getNextCursor(firstSlice);
		pageable = PageRequest.ofSize(5);
		// when
		Slice<Region> regionSlice = regionPaginationRepository.searchBySlice(lastRegionId, regionName, pageable);
		// then
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(regionSlice.getContent()).hasSize(5);
			softAssertions.assertThat(regionSlice.hasNext()).isTrue();
			softAssertions.assertAll();
		});
	}

	private Long getNextCursor(Slice<Region> slice) {
		List<Region> contents = slice.getContent();
		boolean hasNext = slice.hasNext();

		Long nextCursor = null;
		if (hasNext) {
			nextCursor = contents.get(contents.size() - 1).getId();
		}
		return nextCursor;
	}
}
