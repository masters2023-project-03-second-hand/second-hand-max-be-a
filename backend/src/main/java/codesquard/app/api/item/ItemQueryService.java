package codesquard.app.api.item;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.api.errors.errorcode.ItemErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.item.response.ItemDetailResponse;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.item.ItemRepository;
import codesquard.app.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ItemQueryService {

	private final ItemRepository itemRepository;

	/**
	 *
	 * @param itemId            조회하고자 하는 상품의 등록번호
	 * @param loginMemberId        로그인한 사용자의 회원 등록번호
	 */
	public ItemDetailResponse findDetailItemBy(Long itemId, Long loginMemberId) {
		log.info("상품 상세 조회 서비스 요청, 상품 등록번호 : {}, 로그인 회원의 등록번호 : {}", itemId, loginMemberId);
		Item item = itemRepository.findById(itemId)
			.orElseThrow(() -> new RestApiException(ItemErrorCode.ITEM_NOT_FOUND));
		Member seller = item.getMember();
		return createItemDetailResponse(item, seller, loginMemberId);
	}

	private ItemDetailResponse createItemDetailResponse(Item item, Member seller, Long loginMemberId) {
		if (seller.equalId(loginMemberId)) {
			return ItemDetailResponse.createWithSellerResponse(item, seller);
		}
		return ItemDetailResponse.createWithBuyerResponse(item, seller);
	}
}
