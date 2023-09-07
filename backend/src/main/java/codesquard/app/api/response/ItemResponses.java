package codesquard.app.api.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class ItemResponses {

	private List<ItemResponse> contents;
	private Paging paging;

	public ItemResponses(List<ItemResponse> contents, boolean hasNext, Long nextCursor) {
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

