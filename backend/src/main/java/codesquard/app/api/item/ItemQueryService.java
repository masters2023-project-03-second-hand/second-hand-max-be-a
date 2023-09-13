package codesquard.app.api.item;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.api.errors.errorcode.ItemErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.item.response.ItemDetailResponse;
import codesquard.app.domain.image.Image;
import codesquard.app.domain.image.ImageRepository;
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
	private final ImageRepository imageRepository;

	public ItemDetailResponse findDetailItemBy(Long itemId, Long loginMemberId) {
		log.info("상품 상세 조회 서비스 요청, 상품 등록번호 : {}, 로그인 회원의 등록번호 : {}", itemId, loginMemberId);
		Item item = itemRepository.findById(itemId)
			.orElseThrow(() -> new RestApiException(ItemErrorCode.ITEM_NOT_FOUND));
		List<Image> images = imageRepository.findAllByItemId(item.getId());
		List<String> imageUrls = mapToImageUrls(images);
		Member seller = item.getMember();
		return ItemDetailResponse.create(item, seller, loginMemberId, imageUrls);
	}

	private List<String> mapToImageUrls(List<Image> images) {
		return images.stream()
			.map(Image::getImageUrl)
			.collect(Collectors.toUnmodifiableList());
	}
}
