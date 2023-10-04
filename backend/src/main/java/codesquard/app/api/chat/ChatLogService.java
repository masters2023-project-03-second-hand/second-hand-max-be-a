package codesquard.app.api.chat;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.BooleanBuilder;

import codesquard.app.api.chat.request.ChatLogSendRequest;
import codesquard.app.api.chat.response.ChatLogItemResponse;
import codesquard.app.api.chat.response.ChatLogListResponse;
import codesquard.app.api.chat.response.ChatLogMessageResponse;
import codesquard.app.api.chat.response.ChatLogSendResponse;
import codesquard.app.api.errors.errorcode.ErrorCode;
import codesquard.app.api.errors.exception.NotFoundResourceException;
import codesquard.app.domain.chat.ChatLog;
import codesquard.app.domain.chat.ChatLogPaginationRepository;
import codesquard.app.domain.chat.ChatLogRepository;
import codesquard.app.domain.chat.ChatRoom;
import codesquard.app.domain.chat.ChatRoomRepository;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.item.ItemRepository;
import codesquard.app.domain.oauth.support.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatLogService {

	private final ChatLogRepository chatLogRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final ItemRepository itemRepository;
	private final ChatLogPaginationRepository chatLogPaginationRepository;

	@Transactional
	public ChatLogSendResponse sendMessage(ChatLogSendRequest request, Long chatRoomId, Principal sender) {
		ChatRoom chatRoom = findChatRoomBy(chatRoomId);
		ChatLog chatLog = ChatLog.createBySender(request.getMessage(), chatRoom, sender);
		return ChatLogSendResponse.from(chatLogRepository.save(chatLog));
	}

	@Transactional
	public ChatLogListResponse readMessages(Long chatRoomId, Principal principal, Long cursor, Pageable pageable) {
		ChatRoom chatRoom = findChatRoomBy(chatRoomId);
		Item item = findItemBy(chatRoom);

		String chatPartnerName = principal.getChatPartnerName(item, chatRoom);
		BooleanBuilder whereBuilder = new BooleanBuilder();
		whereBuilder.orAllOf(
			chatLogRepository.greaterThanChatLogId(cursor),
			chatLogRepository.equalChatRoomId(chatRoomId));
		Slice<ChatLog> slice = chatLogPaginationRepository.searchBySlice(whereBuilder, pageable);

		List<ChatLog> contents = slice.getContent().stream()
			.collect(Collectors.toUnmodifiableList());
		// 메시지 읽는다.
		contents.forEach(c -> c.decreaseMessageReadCount(principal.getLoginId()));

		List<ChatLogMessageResponse> messageResponses = contents.stream()
			.map(c -> ChatLogMessageResponse.from(c, principal))
			.collect(Collectors.toUnmodifiableList());

		boolean hasNext = slice.hasNext();
		Long nextCursor = getNextCursor(messageResponses, hasNext);

		return new ChatLogListResponse(chatPartnerName, ChatLogItemResponse.from(item), messageResponses, hasNext,
			nextCursor);
	}

	private Long getNextCursor(List<ChatLogMessageResponse> contents, boolean hasNext) {
		Long nextCursor = null;
		if (hasNext) {
			nextCursor = contents.get(contents.size() - 1).getChatLogId();
		}
		return nextCursor;
	}

	private ChatRoom findChatRoomBy(Long chatRoomId) {
		return chatRoomRepository.findById(chatRoomId)
			.orElseThrow(() -> new NotFoundResourceException(ErrorCode.NOT_FOUND_CHATROOM));
	}

	private Item findItemBy(ChatRoom chatRoom) {
		return itemRepository.findById(chatRoom.getItem().getId())
			.orElseThrow(() -> new NotFoundResourceException(ErrorCode.ITEM_NOT_FOUND));
	}
}
