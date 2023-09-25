package codesquard.app.domain.chat;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {

	List<ChatLog> findAllByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId);

	Optional<ChatLog> findChatLogByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId);
}
