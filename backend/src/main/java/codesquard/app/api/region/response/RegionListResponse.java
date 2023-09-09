package codesquard.app.api.region.response;

import static codesquard.app.api.response.ItemResponses.*;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegionListResponse {
	private List<RegionItemResponse> contents;
	private Paging paging;

	private RegionListResponse(List<RegionItemResponse> contents, boolean hasNext, Long nextCursor) {
		this.contents = contents;
		this.paging = Paging.create(nextCursor, hasNext);
	}

	public static RegionListResponse create(List<RegionItemResponse> contents, boolean hasNext, Long nextCursor) {
		return new RegionListResponse(contents, hasNext, nextCursor);
	}
}
