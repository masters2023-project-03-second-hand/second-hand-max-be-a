package codesquard.app.api.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class ItemListResponse {

	private List<ItemResponse> contents;
	private Paging paging;

	public ItemListResponse(List<ItemResponse> contents, boolean hasNext, Long nextCursor) {
		this.contents = contents;
		this.paging = new Paging(nextCursor, hasNext);
	}

	@AllArgsConstructor
	@Getter
	public static class Paging {

		private Long nextCursor;
		private boolean hasNext;
	}
}

