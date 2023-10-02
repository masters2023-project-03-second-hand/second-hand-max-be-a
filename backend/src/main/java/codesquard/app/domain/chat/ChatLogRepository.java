package codesquard.app.domain.chat;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {

	Optional<ChatLog> findFirstByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId);
}
