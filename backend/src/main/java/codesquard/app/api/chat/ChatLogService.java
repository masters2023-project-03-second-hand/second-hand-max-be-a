package codesquard.app.api.chat;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.BooleanBuilder;

import codesquard.app.api.chat.request.ChatLogSendRequest;
import codesquard.app.api.chat.response.ChatLogItemResponse;
import codesquard.app.api.chat.response.ChatLogListResponse;
import codesquard.app.api.chat.response.ChatLogMessageResponse;
import codesquard.app.api.chat.response.ChatLogSendResponse;
import codesquard.app.api.errors.errorcode.ErrorCode;
import codesquard.app.api.errors.exception.ForBiddenException;
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

		if (!principal.isSeller(chatRoom.getSeller()) && !principal.isBuyer(chatRoom.getBuyer())) {
			throw new ForBiddenException(ErrorCode.FORBIDDEN_CHAT_LOG);
		}

		String chatPartnerName = principal.getChatPartnerName(item, chatRoom);
		BooleanBuilder whereBuilder = new BooleanBuilder();
		whereBuilder.orAllOf(
			chatLogRepository.greaterThanChatLogId(cursor),
			chatLogRepository.equalChatRoomId(chatRoomId));
		List<ChatLog> chatLogs = chatLogPaginationRepository.searchBy(whereBuilder);

		// 메시지 읽는다.
		chatLogs.forEach(c -> c.decreaseMessageReadCount(principal.getLoginId()));

		List<ChatLogMessageResponse> messageResponses = chatLogs.stream()
			.map(c -> ChatLogMessageResponse.from(c, principal))
			.collect(Collectors.toUnmodifiableList());

		Long nextMessageId = chatLogs.get(chatLogs.size() - 1).getId();
		return new ChatLogListResponse(chatPartnerName, ChatLogItemResponse.from(item), messageResponses,
			nextMessageId);
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
