package codesquard.app.api.chat;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;

import codesquard.app.api.chat.response.ChatRoomCreateResponse;
import codesquard.app.api.chat.response.ChatRoomItemResponse;
import codesquard.app.api.chat.response.ChatRoomListResponse;
import codesquard.app.api.errors.errorcode.ErrorCode;
import codesquard.app.api.errors.exception.NotFoundResourceException;
import codesquard.app.domain.chat.ChatLog;
import codesquard.app.domain.chat.ChatLogCountRepository;
import codesquard.app.domain.chat.ChatLogRepository;
import codesquard.app.domain.chat.ChatRoom;
import codesquard.app.domain.chat.ChatRoomPaginationRepository;
import codesquard.app.domain.chat.ChatRoomRepository;
import codesquard.app.domain.chat.QChatRoom;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.item.ItemRepository;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.member.MemberRepository;
import codesquard.app.domain.oauth.support.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatRoomService {

	private final ChatRoomRepository chatRoomRepository;
	private final MemberRepository memberRepository;
	private final ItemRepository itemRepository;
	private final ChatRoomPaginationRepository chatRoomPaginationRepository;
	private final ChatLogRepository chatLogRepository;
	private final ChatLogCountRepository chatLogCountRepository;

	@Transactional
	public ChatRoomCreateResponse createChatRoom(Long itemId, Principal sender) {
		log.info("채팅방 생성 서비스 요청 : itemId={}, sender={}", itemId, sender);

		Item item = findItemBy(itemId);
		Member senderMember = findMemberBy(sender.getMemberId());

		ChatRoom chatRoom = new ChatRoom(senderMember, item);

		ChatRoom saveChatRoom = chatRoomRepository.save(chatRoom);
		log.debug("채팅방 저장 결과 : chatRoom={}", saveChatRoom);

		return ChatRoomCreateResponse.from(saveChatRoom);
	}

	private Item findItemBy(Long itemId) {
		return itemRepository.findById(itemId)
			.orElseThrow(() -> new NotFoundResourceException(ErrorCode.ITEM_NOT_FOUND));
	}

	private Member findMemberBy(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new NotFoundResourceException(ErrorCode.NOT_FOUND_MEMBER));
	}

	public ChatRoomListResponse readAllChatRoom(Principal principal, Pageable pageable) {
		// key: 채팅방 등록번호, value: 새로운 메시지 개수
		Map<Long, Long> newMessageMap = chatLogCountRepository.countNewMessage(principal.getLoginId());
		log.debug("채팅방 목록 조회 : newMessageMap={}", newMessageMap);

		List<Long> itemIds = itemRepository.findAllByMemberId(principal.getMemberId()).stream()
			.map(Item::getId)
			.collect(Collectors.toUnmodifiableList());
		log.info("회원이 판매하는 상품 아이디 : {}", itemIds);

		// 1. 내가 팔고 있는 상품들의 등록번호 조회 조건
		BooleanExpression chatRoomOfSellingItemsCondition = QChatRoom.chatRoom.item.id.in(itemIds);
		// 2. 내가 사려고 하는 상품에 대한 채팅방 목록 조회 조건
		BooleanExpression chatRoomOfBuyingItemsCondition = QChatRoom.chatRoom.buyer.id.eq(principal.getMemberId());
		BooleanBuilder whereBuilder = new BooleanBuilder();
		whereBuilder.andAnyOf(chatRoomOfSellingItemsCondition, chatRoomOfBuyingItemsCondition);

		Slice<ChatRoom> slice = chatRoomPaginationRepository.searchBySlice(whereBuilder, pageable);
		log.info("채팅방 페이징 조회 결과 개수 : {}", slice.getSize());

		List<ChatRoomItemResponse> contents = slice.getContent().stream()
			.map(getChatRoomItemResponseMapper(newMessageMap, principal))
			.filter(Objects::nonNull)
			.sorted(Comparator.comparing(ChatRoomItemResponse::getLastSendTime).reversed())
			.collect(Collectors.toUnmodifiableList());
		boolean hasNext = slice.hasNext();
		Long nextCursor = getNextCursor(contents, hasNext);

		return new ChatRoomListResponse(contents, hasNext, nextCursor);
	}

	private Function<ChatRoom, ChatRoomItemResponse> getChatRoomItemResponseMapper(Map<Long, Long> newMessageMap,
		Principal principal) {

		return chatRoom -> {
			ChatLog chatLog = chatLogRepository.findFirstByChatRoomIdOrderByCreatedAtDesc(chatRoom.getId())
				.orElse(null);
			if (chatLog == null) {
				return null;
			}
			return ChatRoomItemResponse.of(
				chatRoom,
				chatRoom.getItem(),
				getChatRoomPartner(principal, chatRoom),
				chatLog,
				newMessageMap.getOrDefault(chatRoom.getId(), 0L));
		};
	}

	private Member getChatRoomPartner(Principal principal, ChatRoom chatRoom) {
		if (principal.isBuyer(chatRoom.getBuyer())) {
			return chatRoom.getSeller();
		}
		return chatRoom.getBuyer();
	}

	private Long getNextCursor(List<ChatRoomItemResponse> contents, boolean hasNext) {
		Long nextCursor = null;
		if (hasNext) {
			nextCursor = contents.get(contents.size() - 1).getChatRoomId();
		}
		return nextCursor;
	}

	public ChatRoomListResponse readAllChatRoomByItem(Long itemId, Principal principal, Pageable pageable) {
		// key: 채팅방 등록번호, value: 새로운 메시지 개수
		Map<Long, Long> newMessageMap = chatLogCountRepository.countNewMessage(principal.getLoginId());
		log.debug("채팅방 목록 조회 : newMessageMap={}", newMessageMap);

		Item item = findItemBy(itemId);
		BooleanBuilder whereBuilder = new BooleanBuilder();
		whereBuilder.andAnyOf(chatRoomRepository.equalItemId(item.getId()));

		Slice<ChatRoom> slice = chatRoomPaginationRepository.searchBySlice(whereBuilder, pageable);

		List<ChatRoomItemResponse> contents = slice.getContent().stream()
			.map(getChatRoomItemResponseMapper(newMessageMap, principal))
			.filter(Objects::nonNull)
			.sorted(Comparator.comparing(ChatRoomItemResponse::getLastSendTime).reversed())
			.collect(Collectors.toUnmodifiableList());
		boolean hasNext = slice.hasNext();
		Long nextCursor = getNextCursor(contents, hasNext);

		return new ChatRoomListResponse(contents, hasNext, nextCursor);
	}
}
