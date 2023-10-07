package codesquard.app.api.wishitem;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.api.errors.errorcode.ItemErrorCode;
import codesquard.app.api.errors.errorcode.MemberErrorCode;
import codesquard.app.api.errors.errorcode.WishErrorCode;
import codesquard.app.api.errors.exception.BadRequestException;
import codesquard.app.api.errors.exception.NotFoundResourceException;
import codesquard.app.api.item.response.ItemResponse;
import codesquard.app.api.item.response.ItemResponses;
import codesquard.app.api.wishitem.response.WishCategoryListResponse;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.item.ItemRepository;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.member.MemberRepository;
import codesquard.app.domain.oauth.support.Principal;
import codesquard.app.domain.pagination.PaginationUtils;
import codesquard.app.domain.wish.Wish;
import codesquard.app.domain.wish.WishPaginationRepository;
import codesquard.app.domain.wish.WishRepository;
import codesquard.app.domain.wish.WishStatus;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
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
			.orElseThrow(() -> new NotFoundResourceException(ItemErrorCode.ITEM_NOT_FOUND));
		if (wishRepository.existsByMemberIdAndItemId(memberId, itemId)) {
			throw new BadRequestException(WishErrorCode.DUPLICATED_REQUEST);
		}
		item.wishRegister();
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new NotFoundResourceException(MemberErrorCode.NOT_FOUND_MEMBER));
		wishRepository.save(new Wish(member, item));
	}

	private void cancel(Long itemId) {
		Item item = itemRepository.findById(itemId)
			.orElseThrow(() -> new NotFoundResourceException(ItemErrorCode.ITEM_NOT_FOUND));
		item.wishCancel();
		List<Long> wishIds = wishRepository.findByItemId(item.getId()).stream()
			.map(Wish::getId)
			.collect(Collectors.toUnmodifiableList());
		wishRepository.deleteAllByIdIn(wishIds);
	}

	public ItemResponses findAll(Long categoryId, int size, Long cursor, Principal principal) {
		Long memberId = principal.getMemberId();
		Slice<ItemResponse> itemResponses = wishPaginationRepository.findAll(categoryId, size, cursor, memberId);
		return PaginationUtils.getItemResponses(itemResponses);
	}

	@Cacheable("wishCategories")
	public WishCategoryListResponse readWishCategories(Principal principal) {
		List<Wish> wishes = wishRepository.findAllByMemberId(principal.getMemberId());
		List<Category> categories = wishes.stream()
			.map(Wish::getItem)
			.map(Item::getCategory)
			.sorted(Comparator.comparing(Category::getId))
			.distinct()
			.collect(Collectors.toUnmodifiableList());
		return WishCategoryListResponse.of(categories);
	}
}
