package codesquard.app.api.region.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RegionListRequest {

	private Long lastRegionId;
	private int size;
	private String region;

	public static RegionListRequest create(Long lastRegionId, int size, String region) {
		return new RegionListRequest(lastRegionId, size, region);
	}
}
