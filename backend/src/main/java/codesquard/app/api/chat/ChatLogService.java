package codesquard.app.api.chat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.api.chat.request.ChatLogSendRequest;
import codesquard.app.api.chat.response.ChatLogItemResponse;
import codesquard.app.api.chat.response.ChatLogListResponse;
import codesquard.app.api.chat.response.ChatLogMessageResponse;
import codesquard.app.api.chat.response.ChatLogSendResponse;
import codesquard.app.api.errors.errorcode.ChatRoomErrorCode;
import codesquard.app.api.errors.errorcode.ItemErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
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

	@SuppressWarnings("checkstyle:SeparatorWrap")
	@Transactional
	public ChatLogListResponse readMessages(Long chatRoomId, int messageIndex, Principal principal, Long cursor,
		int size) {
		ChatRoom chatRoom = findChatRoomBy(chatRoomId);
		Item item = findItemBy(chatRoom);

		String chatPartnerName = getChatPartnerName(principal, item, chatRoom);
		Slice<ChatLog> slice = chatLogPaginationRepository.searchBySlice(cursor, Pageable.ofSize(size));

		List<ChatLog> contents = slice.getContent().stream()
			.collect(Collectors.toUnmodifiableList());

		if (messageIndex < 0 || messageIndex >= contents.size()) {
			return ChatLogListResponse.emptyResponse(chatPartnerName, item);
		}

		List<ChatLog> chatLogs = contents.subList(messageIndex, contents.size());
		// 메시지 읽는다.
		chatLogs.forEach(c -> c.readMessage(principal.getLoginId()));

		List<ChatLogMessageResponse> messageResponses = IntStream.range(0, chatLogs.size())
			.mapToObj(idx -> {
				ChatLog chatLog = chatLogs.get(idx);
				return ChatLogMessageResponse.from(idx, chatLog, principal);
			}).collect(Collectors.toUnmodifiableList());

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
			.orElseThrow(() -> new RestApiException(ChatRoomErrorCode.NOT_FOUND_CHATROOM));
	}

	private Item findItemBy(ChatRoom chatRoom) {
		return itemRepository.findById(chatRoom.getItem().getId())
			.orElseThrow(() -> new RestApiException(ItemErrorCode.ITEM_NOT_FOUND));
	}

	private String getChatPartnerName(Principal principal, Item item, ChatRoom chatRoom) {
		if (principal.isSeller(item.getMember())) {
			return chatRoom.getBuyer().getLoginId();
		}
		return item.getMember().getLoginId();
	}
}
