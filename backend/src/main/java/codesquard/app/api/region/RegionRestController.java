package codesquard.app.api.region;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import codesquard.app.api.region.response.RegionListResponse;
import codesquard.app.api.response.ApiResponse;
import codesquard.app.api.success.successcode.RegionSuccessCode;
import lombok.RequiredArgsConstructor;

@RequestMapping(path = "/api/regions")
@RequiredArgsConstructor
@RestController
public class RegionRestController {

	private final RegionService regionService;

	@GetMapping
	public ApiResponse<RegionListResponse> findAll(
		@RequestParam(required = false, defaultValue = "10") int size,
		@RequestParam(required = false) Long cursor,
		@RequestParam(required = false) String region) {
		return ApiResponse.success(RegionSuccessCode.OK_REGIONS, regionService.searchBySlice(size, cursor, region));
	}
}
