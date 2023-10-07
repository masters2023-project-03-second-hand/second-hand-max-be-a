package codesquard.app.domain.chat;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

	void deleteByItemId(Long itemId);

	@Query("select chatRoom.id from ChatRoom chatRoom where chatRoom.item.id = :itemId and chatRoom.buyer.id = :memberId")
	Optional<Long> findByItemIdAndMemberId(@Param("itemId") Long itemId, @Param("memberId") Long memberId);
}
