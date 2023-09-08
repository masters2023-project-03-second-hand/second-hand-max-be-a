package codesquard.app.api.wishitem;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.api.errors.errorcode.ItemErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.item.ItemRepository;
import codesquard.app.domain.wish.Wish;
import codesquard.app.domain.wish.WishRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WishItemService {

	private final ItemRepository itemRepository;
	private final WishRepository wishRepository;

	@Transactional
	public void register(Long itemId, Long memberId) {
		Item item = itemRepository.findById(itemId)
			.orElseThrow(() -> new RestApiException(ItemErrorCode.ITEM_NOT_FOUND));
		item.wishRegister();
		wishRepository.save(new Wish(memberId, itemId));
	}

	@Transactional
	public void cancel(Long itemId) {
		Item item = itemRepository.findById(itemId)
			.orElseThrow(() -> new RestApiException(ItemErrorCode.ITEM_NOT_FOUND));
		item.wishCancel();
		wishRepository.deleteByItemId(itemId);
	}

	@Transactional
	public List<Wish> findAll(Long categoryId, int size, Long cursor) {
		cursor = cursor == null ? Long.MAX_VALUE : cursor;
		if (categoryId == null) {
			return wishRepository.findAll(cursor, Pageable.ofSize(size));
		} else {
			return wishRepository.findAllByCategoryId(categoryId, cursor, Pageable.ofSize(size));
		}
	}
}
