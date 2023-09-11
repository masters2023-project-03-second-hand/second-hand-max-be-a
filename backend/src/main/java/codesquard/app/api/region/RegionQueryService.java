package codesquard.app.api.region;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.api.region.request.RegionListRequest;
import codesquard.app.api.region.response.RegionItemResponse;
import codesquard.app.api.region.response.RegionListResponse;
import codesquard.app.domain.region.Region;
import codesquard.app.domain.region.RegionPaginationRepository;
import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RegionQueryService {

	private final RegionPaginationRepository regionPaginationRepository;

	public RegionListResponse searchBySlice(RegionListRequest request) {
		Long lastRegionId = request.getLastRegionId();
		String region = request.getRegion();
		Pageable pageable = PageRequest.ofSize(request.getSize());
		Slice<Region> slice = regionPaginationRepository.searchBySlice(lastRegionId, region, pageable);

		List<RegionItemResponse> contents = slice.getContent().stream()
			.map(RegionItemResponse::from)
			.collect(Collectors.toUnmodifiableList());
		boolean hasNext = slice.hasNext();
		Long nextCursor = getNextCursor(contents, hasNext);

		return RegionListResponse.create(contents, hasNext, nextCursor);
	}

	private Long getNextCursor(List<RegionItemResponse> contents, boolean hasNext) {
		Long nextCursor = null;
		if (hasNext) {
			nextCursor = contents.get(contents.size() - 1).getAddressId();
		}
		return nextCursor;
	}
}
