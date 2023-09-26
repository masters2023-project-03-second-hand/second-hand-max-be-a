package codesquard.app.api.chat;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

	@Transactional
	public ChatLogSendResponse sendMessage(ChatLogSendRequest request, Long chatRoomId, Principal sender) {
		ChatRoom chatRoom = findChatRoomBy(chatRoomId);
		ChatLog chatLog = ChatLog.createBySender(request.getMessage(), sender, chatRoom);
		return ChatLogSendResponse.from(chatLogRepository.save(chatLog));
	}

	public ChatLogListResponse readMessages(Long chatRoomId, int messageIndex, Principal principal) {
		ChatRoom chatRoom = findChatRoomBy(chatRoomId);
		Item item = findItemBy(chatRoom);

		String chatPartnerName = getChatPartnerName(principal, item, chatRoom);
		List<ChatLog> chatLogs = chatLogRepository.findAllByChatRoomIdOrderByCreatedAtAsc(chatRoomId);
		log.debug("메시지 읽기에서 채팅 로그 결과 : chatLogs.size={}", chatLogs.size());
		if (messageIndex > chatLogs.size()) {
			return new ChatLogListResponse(chatPartnerName, ChatLogItemResponse.from(item), Collections.emptyList());
		}

		List<ChatLog> chatLogsAfterIndex = chatLogs.subList(messageIndex, chatLogs.size());
		List<ChatLogMessageResponse> chats = IntStream.range(0, chatLogsAfterIndex.size())
			.mapToObj(idx -> {
				ChatLog chatLog = chatLogsAfterIndex.get(idx);
				return ChatLogMessageResponse.from(idx, chatLog, principal);
			}).collect(Collectors.toUnmodifiableList());

		return new ChatLogListResponse(
			chatPartnerName,
			ChatLogItemResponse.from(item),
			chats);
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
