package codesquard.app.api.chat;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.api.chat.request.ChatLogSendRequest;
import codesquard.app.api.chat.response.ChatLogSendResponse;
import codesquard.app.api.errors.errorcode.ChatRoomErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.domain.chat.ChatLog;
import codesquard.app.domain.chat.ChatLogRepository;
import codesquard.app.domain.chat.ChatRoom;
import codesquard.app.domain.chat.ChatRoomRepository;
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

	@Transactional
	public ChatLogSendResponse sendMessage(ChatLogSendRequest request, Long chatRoomId, Principal sender) {
		ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
			.orElseThrow(() -> new RestApiException(ChatRoomErrorCode.NOT_FOUND_CHATROOM));
		String seller = chatRoom.getItem().getMember().getLoginId();

		ChatLog chatLog = new ChatLog(request.getMessage(), sender.getLoginId(), seller, LocalDateTime.now(),
			chatRoom);
		ChatLog saveChatLog = chatLogRepository.save(chatLog);
		return ChatLogSendResponse.from(saveChatLog);
	}
}
