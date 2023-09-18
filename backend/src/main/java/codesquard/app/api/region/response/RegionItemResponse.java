package codesquard.app.api.region.response;

import codesquard.app.domain.region.Region;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RegionItemResponse {

	private Long addressId;
	private String fullAddressName;
	private String addressName;

	public static RegionItemResponse from(Region region) {
		return new RegionItemResponse(region.getId(), region.getName(), region.getShortAddress());
	}
}
