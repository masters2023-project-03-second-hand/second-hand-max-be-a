package codesquard.app.domain.chat;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {
	int countChatLogByChatRoomId(Long chatRoomId);

	List<ChatLog> findAllByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId);
}
