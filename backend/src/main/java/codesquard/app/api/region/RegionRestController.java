package codesquard.app.api.region;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import codesquard.app.api.region.request.RegionListRequest;
import codesquard.app.api.region.response.RegionListResponse;
import codesquard.app.api.response.ApiResponse;
import lombok.RequiredArgsConstructor;

@RequestMapping(path = "/api/regions")
@RequiredArgsConstructor
@RestController
public class RegionRestController {

	private final RegionQueryService regionQueryService;

	@GetMapping
	public ApiResponse<RegionListResponse> findAll(
		@RequestParam("size") int size,
		@RequestParam("cursor") Long cursor,
		@RequestParam("region") String region) {
		RegionListRequest request = RegionListRequest.create(cursor, size, region);
		return ApiResponse.ok("주소 목록 조회에 성공하였습니다.", regionQueryService.searchBySlice(request));
	}
}
