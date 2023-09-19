package codesquard.app.api.wishitem;

import java.time.LocalDateTime;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.api.errors.errorcode.ItemErrorCode;
import codesquard.app.api.errors.errorcode.MemberErrorCode;
import codesquard.app.api.errors.errorcode.WishErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.item.response.ItemResponse;
import codesquard.app.api.item.response.ItemResponses;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.item.ItemRepository;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.member.MemberRepository;
import codesquard.app.domain.pagination.PaginationUtils;
import codesquard.app.domain.wish.Wish;
import codesquard.app.domain.wish.WishPaginationRepository;
import codesquard.app.domain.wish.WishRepository;
import codesquard.app.domain.wish.WishStatus;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WishItemService {

	private final ItemRepository itemRepository;
	private final WishRepository wishRepository;
	private final WishPaginationRepository wishPaginationRepository;
	private final MemberRepository memberRepository;

	@Transactional
	public void changeWishStatus(Long itemId, Long memberId, WishStatus status) {
		if (status == WishStatus.YES) {
			register(itemId, memberId);
			return;
		}
		cancel(itemId);
	}

	private void register(Long itemId, Long memberId) {
		Item item = itemRepository.findById(itemId)
			.orElseThrow(() -> new RestApiException(ItemErrorCode.ITEM_NOT_FOUND));
		if (wishRepository.existsByMemberIdAndItemId(memberId, itemId)) {
			throw new RestApiException(WishErrorCode.DUPLICATED_REQUEST);
		}
		item.wishRegister();
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new RestApiException(MemberErrorCode.NOT_FOUND_MEMBER));
		wishRepository.save(new Wish(member, item, LocalDateTime.now()));
	}

	private void cancel(Long itemId) {
		Item item = itemRepository.findById(itemId)
			.orElseThrow(() -> new RestApiException(ItemErrorCode.ITEM_NOT_FOUND));
		item.wishCancel();
		wishRepository.deleteByItemId(itemId);
	}

	@Transactional(readOnly = true)
	public ItemResponses findAll(Long categoryId, int size, Long cursor) {
		Slice<ItemResponse> itemResponses = wishPaginationRepository.findAll(categoryId, size, cursor);
		return PaginationUtils.getItemResponses(itemResponses);
	}
}
