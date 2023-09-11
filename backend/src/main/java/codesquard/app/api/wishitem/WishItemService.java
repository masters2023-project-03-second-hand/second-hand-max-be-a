package codesquard.app.api.wishitem;

import java.util.List;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.api.errors.errorcode.ItemErrorCode;
import codesquard.app.api.errors.errorcode.WishErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.response.ItemResponse;
import codesquard.app.api.response.ItemResponses;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.item.ItemRepository;
import codesquard.app.domain.wish.Wish;
import codesquard.app.domain.wish.WishPaginationRepository;
import codesquard.app.domain.wish.WishRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WishItemService {

	private final ItemRepository itemRepository;
	private final WishRepository wishRepository;
	private final WishPaginationRepository wishPaginationRepository;

	@Transactional
	public void register(Long itemId, Long memberId, String status) {
		if (status.equals("yes")) {
			Item item = itemRepository.findById(itemId)
				.orElseThrow(() -> new RestApiException(ItemErrorCode.ITEM_NOT_FOUND));
			item.wishRegister();
			wishRepository.save(new Wish(memberId, itemId));
		} else if (status.equals("no")) {
			cancel(itemId);
		} else {
			throw new RestApiException(WishErrorCode.INVALID_PARAMETER);
		}
	}

	@Transactional
	public void cancel(Long itemId) {
		Item item = itemRepository.findById(itemId)
			.orElseThrow(() -> new RestApiException(ItemErrorCode.ITEM_NOT_FOUND));
		item.wishCancel();
		wishRepository.deleteByItemId(itemId);
	}

	@Transactional
	public ItemResponses findAll(Long categoryId, int size, Long cursor) {
		Slice<ItemResponse> itemResponses = wishPaginationRepository.findAll(categoryId, size, cursor);

		List<ItemResponse> contents = itemResponses.getContent();

		boolean hasNext = itemResponses.hasNext();
		Long nextCursor = null;
		if (hasNext) {
			nextCursor = contents.get(contents.size() - 1).getItemId();
		}
		return new ItemResponses(contents, hasNext, nextCursor);
	}
}
