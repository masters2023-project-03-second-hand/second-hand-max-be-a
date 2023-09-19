package codesquard.app.domain.pagination;

import java.util.List;

import org.springframework.data.domain.Slice;

import codesquard.app.api.item.response.ItemResponse;
import codesquard.app.api.item.response.ItemResponses;

public class PaginationUtils {

	public static ItemResponses getItemResponses(Slice<ItemResponse> itemResponses) {
		List<ItemResponse> contents = itemResponses.getContent();

		boolean hasNext = itemResponses.hasNext();
		Long nextCursor = null;
		if (hasNext) {
			nextCursor = contents.get(contents.size() - 1).getItemId();
		}
		return new ItemResponses(contents, hasNext, nextCursor);
	}
}
