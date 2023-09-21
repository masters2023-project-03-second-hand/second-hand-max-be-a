package codesquard.app.domain.chat;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
	List<ChatRoom> findAllByItemId(Long itemId);

	int deleteByItemId(Long itemId);
}
