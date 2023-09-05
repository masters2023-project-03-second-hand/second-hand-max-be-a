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

	public ItemDetailResponse findDetailItemWithSeller(Long itemId) {
		log.info("상품 상세 조회 서비스 요청, 상품 등록번호 : {}", itemId);
		Item item = itemRepository.findById(itemId)
			.orElseThrow(() -> new RestApiException(ItemErrorCode.NOT_FOUND_ITEM));
		log.debug("{}", item);
		Member seller = item.getMember();
		log.debug("{}", seller);
		return ItemDetailResponse.createWithSellerResponse(item, seller);
	}
}
