package codesquard.app.api.item;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.api.errors.errorcode.ItemErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.item.response.ItemDetailResponse;
import codesquard.app.domain.chat.ChatLogRepository;
import codesquard.app.domain.chat.ChatRoom;
import codesquard.app.domain.chat.ChatRoomRepository;
import codesquard.app.domain.image.Image;
import codesquard.app.domain.image.ImageRepository;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.item.ItemRepository;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.wish.WishRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ItemQueryService {

	private final ItemRepository itemRepository;
	private final ImageRepository imageRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final ChatLogRepository chatLogRepository;
	private final WishRepository wishRepository;

	public ItemDetailResponse findDetailItemBy(Long itemId, Long loginMemberId) {
		log.info("상품 상세 조회 서비스 요청, 상품 등록번호 : {}, 로그인 회원의 등록번호 : {}", itemId, loginMemberId);
		Item item = itemRepository.findById(itemId)
			.orElseThrow(() -> new RestApiException(ItemErrorCode.ITEM_NOT_FOUND));
		List<String> imageUrls = mapToImageUrls(item);
		Member seller = item.getMember();
		int chatCount = getChatCount(item);
		int wishCount = getWishCount(item);

		return ItemDetailResponse.create(item, seller, loginMemberId, imageUrls, chatCount, wishCount);
	}

	private List<String> mapToImageUrls(Item item) {
		List<Image> images = imageRepository.findAllByItemId(item.getId());
		return images.stream()
			.map(Image::getImageUrl)
			.collect(Collectors.toUnmodifiableList());
	}

	private int getChatCount(Item item) {
		List<ChatRoom> chatRooms = chatRoomRepository.findAllByItemId(item.getId());
		return chatRooms.stream()
			.mapToInt(chatRoom -> chatLogRepository.countChatLogByChatRoomId(chatRoom.getId()))
			.sum();
	}

	private int getWishCount(Item item) {
		return wishRepository.countWishByItemId(item.getId());
	}
}
