package codesquard.app.api.region.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegionListRequest {

	private Long lastRegionId;
	private int size;
	private String region;

	private RegionListRequest(Long lastRegionId, int size, String region) {
		this.lastRegionId = lastRegionId;
		this.size = size;
		this.region = region;
	}

	public static RegionListRequest create(Long lastRegionId, int size, String region) {
		return new RegionListRequest(lastRegionId, size, region);
	}
}
