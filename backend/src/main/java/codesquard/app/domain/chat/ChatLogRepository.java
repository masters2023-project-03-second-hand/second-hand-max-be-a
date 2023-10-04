package codesquard.app.domain.chat;

import static codesquard.app.domain.chat.QChatLog.*;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.querydsl.core.types.dsl.BooleanExpression;

public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {

	default BooleanExpression greaterThanChatLogId(Long chatLogId) {
		if (chatLogId == null) {
			return null;
		}
		return chatLog.id.gt(chatLogId);
	}

	default BooleanExpression equalChatRoomId(Long chatRoomId) {
		if (chatRoomId == null) {
			return null;
		}
		return chatLog.chatRoom.id.eq(chatRoomId);
	}

	Optional<ChatLog> findFirstByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId);
}
