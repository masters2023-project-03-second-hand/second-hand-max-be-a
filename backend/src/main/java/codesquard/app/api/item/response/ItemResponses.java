package codesquard.app.api.item.response;

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

		public static Paging create(Long nextCursor, boolean hasNext) {
			return new Paging(nextCursor, hasNext);
		}
	}
}

